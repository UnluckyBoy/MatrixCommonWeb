package com.cloudstudio.matrix.matrixcommonweb.model.Applet;

import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName：ShopItem
 * @Author: matrix
 * @Date: 2026/1/13 19:51
 * @Description:商品类
 */
@Data
public class ShopItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String image;
    private String title;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String sales;
    private List<TagType> tagTypes = new ArrayList<>();
    private List<String> customTags = new ArrayList<>();

    // 标签类型枚举
    @Getter
    public enum TagType {
        HOT("热销"),
        NEW("新品"),
        DISCOUNT("折扣"),
        RECOMMEND("推荐"),
        LIMITED("限量"),
        FREE_SHIPPING("包邮");

        private final String displayName;

        TagType(String displayName) {
            this.displayName = displayName;
        }

    }

    // 添加结构化标签
    public void addTagType(TagType tagType) {
        if (this.tagTypes == null) {
            this.tagTypes = new ArrayList<>();
        }
        this.tagTypes.add(tagType);
    }
}
