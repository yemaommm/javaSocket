package so.sao.shop.gpssocket.Dto;

/**
 * 经纬度
 * @author negocat on 2017/10/30.
 */
public class CoordinateDto {

    /**
     * 经纬度的 度
     */
    private int degree;
    /**
     * 经纬度的 秒
     */
    private float second;

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public float getSecond() {
        return second;
    }

    public void setSecond(float second) {
        this.second = second;
    }

    public CoordinateDto(int degree, float second) {
        this.degree = degree;
        this.second = second;
    }

    @Override
    public String toString() {
        return "CoordinateDto{" +
                "degree=" + degree +
                ", second=" + second +
                '}';
    }
}
