package com.wondertek.mam.util.dataMigration;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataMigrationAnnotation {
    String[] xmlElementName() default {};       // 当前modal字段 对应xml中的元素标签名称
    String xmlParentElementName() default ""; // xml中此节点的所有父节点,以"."分隔
    String migrationType() default "basic";   // "basic":基本类型构成的属性; "modal":自定义pojo类构成的属性; "list":List集合构成的属性; "map":Map集合构成的属性(暂不支持);
    String[] attrType() default {};           // 键值类型转换 [key,value] 如 {"1:男","2:女"}
    String attrModalTypeSplitRegex() default "";   // attrType中键值 拆分标记
    String attrXmlTypeSplitRegex() default "";   // xml多数据 字符串 拆分标记
}
