package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.*;
import com.example.javabackenddemo.dto.response.*;
import com.example.javabackenddemo.entity.*;
import com.example.javabackenddemo.enums.ProductStatus;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.*;
import com.example.javabackenddemo.service.CategoryService;
import com.example.javabackenddemo.service.CurrencyService;
import com.example.javabackenddemo.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductAttributeRepository attributeRepository;
    private final ProductTranslationRepository translationRepository;
    private final SkuRepository skuRepository;
    private final SkuSpecificationRepository specRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductAttributeRepository attributeRepository,
                              ProductTranslationRepository translationRepository,
                              SkuRepository skuRepository,
                              SkuSpecificationRepository specRepository,
                              InventoryRepository inventoryRepository,
                              CategoryService categoryService,
                              CurrencyService currencyService) {
        this.productRepository = productRepository;
        this.attributeRepository = attributeRepository;
        this.translationRepository = translationRepository;
        this.skuRepository = skuRepository;
        this.specRepository = specRepository;
        this.inventoryRepository = inventoryRepository;
        this.categoryService = categoryService;
        this.currencyService = currencyService;
    }

    @Override
    public Page<ProductListItemResponse> listProducts(Pageable pageable, String currency, String lang) {
        Page<Product> products = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);
        return products.map(p -> toListItem(p, currency, lang));
    }

    @Override
    public Page<ProductListItemResponse> listByCategory(Long categoryId, Pageable pageable, String currency, String lang) {
        List<Long> categoryIds = categoryService.getAllChildCategoryIds(categoryId);
        Page<Product> products = productRepository.findByStatusAndCategoryIdIn(ProductStatus.ACTIVE, categoryIds, pageable);
        return products.map(p -> toListItem(p, currency, lang));
    }

    @Override
    public Page<ProductListItemResponse> search(String keyword, Pageable pageable, String currency, String lang) {
        Page<Product> products = productRepository.searchByKeyword(ProductStatus.ACTIVE, keyword, pageable);
        return products.map(p -> toListItem(p, currency, lang));
    }

    @Override
    public ProductDetailResponse getProductDetail(Long id, String currency, String lang) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        String name = product.getName();
        String description = product.getDescription();
        if (lang != null) {
            translationRepository.findByProductIdAndLanguageCode(id, lang).ifPresent(t -> {});
            var translation = translationRepository.findByProductIdAndLanguageCode(id, lang);
            if (translation.isPresent()) {
                name = translation.get().getName();
                description = translation.get().getDescription();
            }
        }
        Category category = categoryService.getCategoryById(product.getCategoryId());
        List<AttributeResponse> attrs = attributeRepository.findByProductId(id).stream()
                .map(a -> new AttributeResponse(a.getAttrName(), a.getAttrValue())).toList();
        List<SkuResponse> skuResponses = skuRepository.findByProductId(id).stream().map(sku -> {
            List<SpecificationResponse> specs = specRepository.findBySkuId(sku.getId()).stream()
                    .map(s -> new SpecificationResponse(s.getSpecName(), s.getSpecValue())).toList();
            Inventory inv = inventoryRepository.findBySkuId(sku.getId()).orElse(null);
            int stock = inv != null ? inv.getQuantity() : 0;
            BigDecimal price = convertPrice(sku.getPrice(), product.getBaseCurrency(), currency);
            return new SkuResponse(sku.getId(), sku.getSkuCode(), price, specs, stock, stock > 0);
        }).toList();
        String cur = currency != null ? currency : product.getBaseCurrency();
        return new ProductDetailResponse(product.getId(), name, description, product.getMainImage(),
                product.getCategoryId(), category.getName(), attrs, skuResponses, cur);
    }

    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .mainImage(request.mainImage())
                .categoryId(request.categoryId())
                .baseCurrency(request.baseCurrency() != null ? request.baseCurrency() : "CNY")
                .build();
        product = productRepository.save(product);
        if (request.attributes() != null) {
            for (AttributeRequest attr : request.attributes()) {
                ProductAttribute pa = ProductAttribute.builder()
                        .productId(product.getId())
                        .attrName(attr.attrName())
                        .attrValue(attr.attrValue())
                        .build();
                attributeRepository.save(pa);
            }
        }
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.mainImage() != null) product.setMainImage(request.mainImage());
        if (request.categoryId() != null) product.setCategoryId(request.categoryId());
        if (request.attributes() != null) {
            attributeRepository.deleteByProductId(id);
            for (AttributeRequest attr : request.attributes()) {
                attributeRepository.save(ProductAttribute.builder()
                        .productId(id).attrName(attr.attrName()).attrValue(attr.attrValue()).build());
            }
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    public Page<ProductListItemResponse> adminList(String name, Long categoryId, ProductStatus status, Pageable pageable) {
        return productRepository.findByFilters(name, categoryId, status, pageable)
                .map(p -> new ProductListItemResponse(p.getId(), p.getName(), p.getMainImage(),
                        getMinPrice(p.getId()), p.getBaseCurrency()));
    }

    @Override
    @Transactional
    public void addSku(Long productId, CreateSkuRequest request) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Sku sku = Sku.builder()
                .productId(productId)
                .skuCode(request.skuCode())
                .price(request.price())
                .build();
        sku = skuRepository.save(sku);
        if (request.specifications() != null) {
            for (SpecificationRequest spec : request.specifications()) {
                specRepository.save(SkuSpecification.builder()
                        .skuId(sku.getId()).specName(spec.specName()).specValue(spec.specValue()).build());
            }
        }
        Inventory inventory = Inventory.builder()
                .sku(sku)
                .quantity(request.initialStock() != null ? request.initialStock() : 0)
                .build();
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void updateSku(Long productId, Long skuId, UpdateSkuRequest request) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("SKU not found: " + skuId));
        if (request.price() != null) sku.setPrice(request.price());
        skuRepository.save(sku);
    }

    @Override
    @Transactional
    public void deleteSku(Long productId, Long skuId) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("SKU not found: " + skuId));
        skuRepository.delete(sku);
    }

    @Override
    @Transactional
    public void addTranslation(Long productId, TranslationRequest request) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        ProductTranslation translation = translationRepository
                .findByProductIdAndLanguageCode(productId, request.languageCode())
                .orElse(ProductTranslation.builder().productId(productId).languageCode(request.languageCode()).build());
        translation.setName(request.name());
        translation.setDescription(request.description());
        translationRepository.save(translation);
    }

    private ProductListItemResponse toListItem(Product product, String currency, String lang) {
        String name = product.getName();
        if (lang != null) {
            var t = translationRepository.findByProductIdAndLanguageCode(product.getId(), lang);
            if (t.isPresent()) name = t.get().getName();
        }
        BigDecimal price = getMinPrice(product.getId());
        price = convertPrice(price, product.getBaseCurrency(), currency);
        String cur = currency != null ? currency : product.getBaseCurrency();
        return new ProductListItemResponse(product.getId(), name, product.getMainImage(), price, cur);
    }

    private BigDecimal getMinPrice(Long productId) {
        List<Sku> skus = skuRepository.findByProductId(productId);
        return skus.stream().map(Sku::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private BigDecimal convertPrice(BigDecimal price, String baseCurrency, String targetCurrency) {
        if (targetCurrency == null || targetCurrency.equals(baseCurrency)) return price;
        return currencyService.convert(price, baseCurrency, targetCurrency);
    }
}
