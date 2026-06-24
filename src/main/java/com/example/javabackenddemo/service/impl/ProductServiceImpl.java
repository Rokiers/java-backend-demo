package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.*;
import com.example.javabackenddemo.dto.response.*;
import com.example.javabackenddemo.entity.*;
import com.example.javabackenddemo.enums.ProductStatus;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.*;
import com.example.javabackenddemo.service.CategoryService;
import com.example.javabackenddemo.service.CurrencyService;
import com.example.javabackenddemo.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductAttributeMapper attributeMapper;
    private final ProductTranslationMapper translationMapper;
    private final SkuMapper skuMapper;
    private final SkuSpecificationMapper specMapper;
    private final InventoryMapper inventoryMapper;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;

    public ProductServiceImpl(ProductMapper productMapper,
                              ProductAttributeMapper attributeMapper,
                              ProductTranslationMapper translationMapper,
                              SkuMapper skuMapper,
                              SkuSpecificationMapper specMapper,
                              InventoryMapper inventoryMapper,
                              CategoryService categoryService,
                              CurrencyService currencyService) {
        this.productMapper = productMapper;
        this.attributeMapper = attributeMapper;
        this.translationMapper = translationMapper;
        this.skuMapper = skuMapper;
        this.specMapper = specMapper;
        this.inventoryMapper = inventoryMapper;
        this.categoryService = categoryService;
        this.currencyService = currencyService;
    }

    private Page<Product> toMybatisPage(int page, int size) {
        return new Page<>(page + 1, size);
    }

    @Override
    public Page<ProductListItemResponse> listProducts(int page, int size, String currency, String lang) {
        Page<Product> products = productMapper.selectPage(toMybatisPage(page, size),
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, ProductStatus.ACTIVE));
        return products.getRecords().stream().map(p -> toListItem(p, currency, lang))
                .collect(java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.toList(),
                        list -> new Page<ProductListItemResponse>(page, size, products.getTotal()).setRecords(list)));
    }

    @Override
    public Page<ProductListItemResponse> listByCategory(Long categoryId, int page, int size, String currency, String lang) {
        List<Long> categoryIds = categoryService.getAllChildCategoryIds(categoryId);
        Page<Product> products = productMapper.selectPage(toMybatisPage(page, size),
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, ProductStatus.ACTIVE)
                        .in(Product::getCategoryId, categoryIds));
        return products.getRecords().stream().map(p -> toListItem(p, currency, lang))
                .collect(java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.toList(),
                        list -> new Page<ProductListItemResponse>(page, size, products.getTotal()).setRecords(list)));
    }

    @Override
    public Page<ProductListItemResponse> search(String keyword, int page, int size, String currency, String lang) {
        Page<Product> myPage = new Page<>(page + 1, size);
        Page<Product> products = productMapper.searchByKeyword(myPage, ProductStatus.ACTIVE.name(), keyword);
        return products.getRecords().stream().map(p -> toListItem(p, currency, lang))
                .collect(java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.toList(),
                        list -> new Page<ProductListItemResponse>(page, size, products.getTotal()).setRecords(list)));
    }

    @Override
    public ProductDetailResponse getProductDetail(Long id, String currency, String lang) {
        Product product = productMapper.selectById(id);
        if (product == null) throw new ResourceNotFoundException("Product not found: " + id);
        String name = product.getName();
        String description = product.getDescription();
        if (lang != null) {
            var wrapper = new LambdaQueryWrapper<ProductTranslation>()
                    .eq(ProductTranslation::getProductId, id)
                    .eq(ProductTranslation::getLanguageCode, lang);
            ProductTranslation translation = translationMapper.selectOne(wrapper);
            if (translation != null) {
                name = translation.getName();
                description = translation.getDescription();
            }
        }
        Category category = categoryService.getCategoryById(product.getCategoryId());
        List<AttributeResponse> attrs = attributeMapper.selectList(
                new LambdaQueryWrapper<ProductAttribute>().eq(ProductAttribute::getProductId, id))
                .stream().map(a -> new AttributeResponse(a.getAttrName(), a.getAttrValue())).toList();
        List<SkuResponse> skuResponses = skuMapper.selectList(
                new LambdaQueryWrapper<Sku>().eq(Sku::getProductId, id)).stream().map(sku -> {
            List<SpecificationResponse> specs = specMapper.selectList(
                    new LambdaQueryWrapper<SkuSpecification>().eq(SkuSpecification::getSkuId, sku.getId()))
                    .stream().map(s -> new SpecificationResponse(s.getSpecName(), s.getSpecValue())).toList();
            Inventory inv = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, sku.getId()));
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
        productMapper.insert(product);
        if (request.attributes() != null) {
            for (AttributeRequest attr : request.attributes()) {
                attributeMapper.insert(ProductAttribute.builder()
                        .productId(product.getId())
                        .attrName(attr.attrName())
                        .attrValue(attr.attrValue())
                        .build());
            }
        }
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = productMapper.selectById(id);
        if (product == null) throw new ResourceNotFoundException("Product not found: " + id);
        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.mainImage() != null) product.setMainImage(request.mainImage());
        if (request.categoryId() != null) product.setCategoryId(request.categoryId());
        if (request.attributes() != null) {
            attributeMapper.delete(new LambdaQueryWrapper<ProductAttribute>().eq(ProductAttribute::getProductId, id));
            for (AttributeRequest attr : request.attributes()) {
                attributeMapper.insert(ProductAttribute.builder()
                        .productId(id).attrName(attr.attrName()).attrValue(attr.attrValue()).build());
            }
        }
        productMapper.updateById(product);
        return product;
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) throw new ResourceNotFoundException("Product not found: " + id);
        product.setStatus(ProductStatus.INACTIVE);
        productMapper.updateById(product);
    }

    @Override
    public Page<ProductListItemResponse> adminList(String name, Long categoryId, ProductStatus status, int page, int size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (name != null) wrapper.like(Product::getName, name);
        if (categoryId != null) wrapper.eq(Product::getCategoryId, categoryId);
        if (status != null) wrapper.eq(Product::getStatus, status);
        Page<Product> products = productMapper.selectPage(toMybatisPage(page, size), wrapper);
        return products.getRecords().stream()
                .map(p -> new ProductListItemResponse(p.getId(), p.getName(), p.getMainImage(),
                        getMinPrice(p.getId()), p.getBaseCurrency()))
                .collect(java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.toList(),
                        list -> new Page<ProductListItemResponse>(page, size, products.getTotal()).setRecords(list)));
    }

    @Override
    @Transactional
    public void addSku(Long productId, CreateSkuRequest request) {
        if (productMapper.selectById(productId) == null)
            throw new ResourceNotFoundException("Product not found: " + productId);
        Sku sku = Sku.builder()
                .productId(productId)
                .skuCode(request.skuCode())
                .price(request.price())
                .build();
        skuMapper.insert(sku);
        if (request.specifications() != null) {
            for (SpecificationRequest spec : request.specifications()) {
                specMapper.insert(SkuSpecification.builder()
                        .skuId(sku.getId()).specName(spec.specName()).specValue(spec.specValue()).build());
            }
        }
        inventoryMapper.insert(Inventory.builder()
                .skuId(sku.getId())
                .quantity(request.initialStock() != null ? request.initialStock() : 0)
                .build());
    }

    @Override
    @Transactional
    public void updateSku(Long productId, Long skuId, UpdateSkuRequest request) {
        Sku sku = skuMapper.selectById(skuId);
        if (sku == null) throw new ResourceNotFoundException("SKU not found: " + skuId);
        if (request.price() != null) sku.setPrice(request.price());
        skuMapper.updateById(sku);
    }

    @Override
    @Transactional
    public void deleteSku(Long productId, Long skuId) {
        Sku sku = skuMapper.selectById(skuId);
        if (sku == null) throw new ResourceNotFoundException("SKU not found: " + skuId);
        skuMapper.deleteById(skuId);
    }

    @Override
    @Transactional
    public void addTranslation(Long productId, TranslationRequest request) {
        if (productMapper.selectById(productId) == null)
            throw new ResourceNotFoundException("Product not found: " + productId);
        var wrapper = new LambdaQueryWrapper<ProductTranslation>()
                .eq(ProductTranslation::getProductId, productId)
                .eq(ProductTranslation::getLanguageCode, request.languageCode());
        ProductTranslation translation = translationMapper.selectOne(wrapper);
        if (translation == null) {
            translation = ProductTranslation.builder()
                    .productId(productId).languageCode(request.languageCode()).build();
        }
        translation.setName(request.name());
        translation.setDescription(request.description());
        if (translation.getId() == null) {
            translationMapper.insert(translation);
        } else {
            translationMapper.updateById(translation);
        }
    }

    private ProductListItemResponse toListItem(Product product, String currency, String lang) {
        String name = product.getName();
        if (lang != null) {
            var t = translationMapper.selectOne(new LambdaQueryWrapper<ProductTranslation>()
                    .eq(ProductTranslation::getProductId, product.getId())
                    .eq(ProductTranslation::getLanguageCode, lang));
            if (t != null) name = t.getName();
        }
        BigDecimal price = getMinPrice(product.getId());
        price = convertPrice(price, product.getBaseCurrency(), currency);
        String cur = currency != null ? currency : product.getBaseCurrency();
        return new ProductListItemResponse(product.getId(), name, product.getMainImage(), price, cur);
    }

    private BigDecimal getMinPrice(Long productId) {
        List<Sku> skus = skuMapper.selectList(new LambdaQueryWrapper<Sku>().eq(Sku::getProductId, productId));
        return skus.stream().map(Sku::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private BigDecimal convertPrice(BigDecimal price, String baseCurrency, String targetCurrency) {
        if (targetCurrency == null || targetCurrency.equals(baseCurrency)) return price;
        return currencyService.convert(price, baseCurrency, targetCurrency);
    }
}
