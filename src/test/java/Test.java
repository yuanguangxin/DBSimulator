import com.match.SqlParse;
import com.util.DBError;

import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        System.out.println("Welcome to the MySQL monitor.");
        Scanner in = new Scanner(System.in);
        String sql;
        SqlParse parse = new SqlParse();
        while (!(sql = in.nextLine()).equals("exit")) {
            parse.setSqlString(sql);
            try {
                parse.parse();
            } catch (DBError dbError) {
                dbError.printStackTrace();
            }
        }
        System.out.println("Bye.");
    }
}
