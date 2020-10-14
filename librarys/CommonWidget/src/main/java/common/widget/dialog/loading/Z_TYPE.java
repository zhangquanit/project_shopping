package common.widget.dialog.loading;

/**
 * Created by zyao89 on 2017/3/19.
 * Contact me at 305161066@qq.com or zyao89@gmail.com
 * For more projects: https://github.com/zyao89
 * My Blog: http://zyao89.me
 */
public enum Z_TYPE {
    CIRCLE(Object.class),
    CIRCLE_CLOCK(Object.class),
    STAR_LOADING(Object.class),
    LEAF_ROTATE(Object.class),
    DOUBLE_CIRCLE(DoubleCircleBuilder.class),
    PAC_MAN(Object.class),
    ELASTIC_BALL(Object.class),
    INFECTION_BALL(Object.class),
    INTERTWINE(Object.class),
    TEXT(Object.class),
    SEARCH_PATH(Object.class),
    ROTATE_CIRCLE(Object.class),
    SINGLE_CIRCLE(SingleCircleBuilder.class),
    SNAKE_CIRCLE(SnakeCircleBuilder.class);

    private final Class<?> mBuilderClass;

    Z_TYPE(Class<?> builderClass) {
        this.mBuilderClass = builderClass;
    }

    <T extends ZLoadingBuilder> T newInstance() {
        try {
            return (T) mBuilderClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
