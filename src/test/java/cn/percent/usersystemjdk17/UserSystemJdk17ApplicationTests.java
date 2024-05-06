package cn.percent.usersystemjdk17;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserSystemJdk17ApplicationTests {

    @Test
    void contextLoads() {

    }


    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     *
     * @param num int整型一维数组 
     * @return int整型ArrayList<ArrayList<>>
     */
    public ArrayList<ArrayList<Integer>> permute (int[] num) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        sort(num, result);
        return result;
    }
    public void sort(int[] num, ArrayList<ArrayList<Integer>>  result) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < num.length; i++) {
            // 集合中存在就不在向里面加了
            if (arrayList.contains(num[i])) {
                continue;
            }
            // 不存在存进去
            arrayList.add(num[i]);
            // 重复此操作
            sort(num, result);
            result.add(arrayList);
        }

    }
    

}
