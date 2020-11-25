//package artoria.random;
//
//import artoria.util.ObjectUtils;
//
//public class SimpleAgeRandomizer implements Randomizer {
//    private Integer maxValue = 85;
//    private Integer minValue = 10;
//
//    public Integer getMaxValue() {
//
//        return maxValue;
//    }
//
//    public void setMaxValue(Integer maxValue) {
//
//        this.maxValue = maxValue;
//    }
//
//    public Integer getMinValue() {
//
//        return minValue;
//    }
//
//    public void setMinValue(Integer minValue) {
//
//        this.minValue = minValue;
//    }
//
//    @Override
//    public <T> T nextObject(Class<T> clazz) {
//        if (!Number.class.isAssignableFrom(clazz)) {
//            return null;
//        }
//        int bound = maxValue - minValue;
//        int nextInt = RandomUtils.nextInt(bound);
//        nextInt = nextInt + minValue;
//        return ObjectUtils.cast(nextInt, clazz);
//    }
//
//}
