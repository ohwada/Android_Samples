/**
 *  Kryo Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.kryosample;

/**
 *  class SampleData
* original https://github.com/keiji/serializer_benchmarks/tree/kryo
 */
public class SampleData {

    public enum Gender {
        Female,
        Male;
    }

    private long id;

    private String name;

    private int age;

    private Gender gender;

    private boolean isMegane;

    public SampleData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isMegane() {
        return isMegane;
    }

    public void setMegane(boolean megane) {
        isMegane = megane;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleData that = (SampleData) o;

        if (id != that.id) return false;
        if (age != that.age) return false;
        if (isMegane != that.isMegane) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return gender == that.gender;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (isMegane ? 1 : 0);
        return result;
    }

}
