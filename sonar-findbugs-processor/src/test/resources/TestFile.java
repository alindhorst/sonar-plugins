
public class TestFile {

    private static Integer number=0;
    public void test() {
        int index=52;
        while(index < 45) number++;
        test();
    }
}