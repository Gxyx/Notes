/**
 * @Auther: Gxyx
 * @Date: 2021/08/07/15:02
 */

public class Student implements Cloneable {

    public String name;
    public int age;
    public ChildClass child;
    @Override
    public Object clone(){
        try {
            //浅拷贝
            //return super.clone();
            //深拷贝
            Student cloneStudent = (Student) super.clone();
            cloneStudent.child = (ChildClass) this.child.clone();
            return cloneStudent;

        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    public Student() {
    }

    public Student(String name, int age, ChildClass child) {
        this.name = name;
        this.age = age;
        this.child = child;
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

    public ChildClass getChild() {
        return child;
    }

    public void setChild(ChildClass child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", child=" + child +
                '}';
    }
}
