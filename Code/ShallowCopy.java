/**
 * @Auther: Gxyx
 * @Date: 2021/08/07/15:14
 */
public class ShallowCopy {
    /**
     * 浅拷贝：
     *       对于基本数据类型进行值的传递
     *       对于引用数据类型进行引用传递的拷贝
     *       s
     */
    public static void main(String[] args) {
        Student student1 = new Student();
        student1.name = "张三";
        student1.age = 30;
        student1.child = new ChildClass();
        student1.child.name= "小张三";
        student1.child.age= 5;

        Student student2 = (Student) student1.clone();

        Student student3 = (Student) student1.clone();

        System.out.println("student1 == student2：" + (student1 == student2));
        System.out.println("student1 hash：" + student1.hashCode());
        System.out.println("student2 hash：" + student2.hashCode());
        System.out.println("student1 name ："+ student1.name);
        System.out.println("student2 name ："+ student2.name);
        System.out.println("==========================");
        System.out.println("student1.child == student3.child：" + (student1.child == student3.child));
        System.out.println("student1.child hash：" + student1.child.hashCode());
        System.out.println("student3.child hash：" + student3.child.hashCode());
    }


}
