/**
 * @Auther: Gxyx
 * @Date: 2021/08/07/15:35
 */
public class ChildClass implements Cloneable {
    public String name;
    public int age;


    @Override
    public Object clone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }


    public ChildClass() {
    }

    public ChildClass(String name, int age) {
        this.name = name;
        this.age = age;
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

    @Override
    public String toString() {
        return "ChildClass{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
