import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Gxyx
 * @Date: 2021/08/07/15:00
 * 拷贝
 */
public class Copy {
    public static void main(String[] args) {
        //引用拷贝
       Student student1 = new Student();
       Student student2 = student1;
       System.out.println(student1);
       System.out.println(student2);

       //对象拷贝
       Student student3 = new Student();
       Student student4 = (Student)student3.clone();
       System.out.println(student3);
       System.out.println(student4);





    }
}
