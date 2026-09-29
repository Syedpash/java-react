import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Problems {

    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                return new int[] { nums[i], complement };
            }
            map.put(nums[i], i);
            // for (int j=i+1; j<nums.length; j++) {
            // if( nums[i] + nums[j] == target) {
            // return new int[] {nums[i], nums[j]};
            // }
            // }
        }
        return new int[] {};
    }

    // Input: "()[]{}" → true
    public static boolean pattern(String str) {
        Stack<Character> stack = new Stack<>();
        for (char c : str.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (stack.isEmpty() ||
                    c == ')' && stack.pop() != '(' ||
                    c == ']' && stack.pop() != '[' ||
                    c == '}' && stack.pop() != '{')
                return false;
        }

        return stack.isEmpty();
    }
    // Given an array, return true if any value appears more than once.

    // Input: [1, 2, 3, 1] → true
    // Input: [1, 2, 3, 4] → false

    public static boolean duplicates(int[] nums) {

        Set<Integer> set = new HashSet<>();

        for (int num : nums) {
            if(set.contains(num)) return true;
            set.add(num);
        }
        // Map<Integer, Integer> map = new HashMap<>();

        // for(int num : nums) {
        //     if(map.containsKey(num)) return true;
        //     else 
        //     map.put(num, (map.get(num) | 0) + 1);
        // }
        return false;
    }

    public static void main(String[] args) {
        // System.out.println(Arrays.toString(twoSum(new int[] { 2, 7, 11, 15 }, 9)));
        // System.out.println(pattern("()[]{}"));
        System.out.println(duplicates(new int[] { 1, 2, 3, 1 }));
    }
}
 