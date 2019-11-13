public class MyBigInteger {

    public String value;

    public MyBigInteger() {
        this.value = "0";
    }

    public MyBigInteger(String value) {
        this.value = value;
    }

    public MyBigInteger plus(MyBigInteger x) {     //"this" will be str1, arg will be str2
        // Before proceeding further, make sure length
        // of str2 is larger.
        if (this.value.length() > x.value.length()){
            String t = this.value;
            this.value = x.value;
            x.value = t;
        }

        // Take an empty String for storing result
        String str = "";

        // Calculate length of both String
        int n1 = this.value.length(), n2 = x.value.length();
        int diff = n2 - n1;

        // Initially take carry zero
        int carry = 0;

        // Traverse from end of both Strings
        for (int i = n1 - 1; i>=0; i--) {
            // compute sum of
            // current digits and carry
            int sum = ((int)(this.value.charAt(i)-'0') +
                    (int)(x.value.charAt(i+diff)-'0') + carry);
            str += (char)(sum % 10 + '0');
            carry = sum / 10;
        }

        // Add remaining digits of str2[]
        for (int i = n2 - n1 - 1; i >= 0; i--) {
            int sum = ((int)(x.value.charAt(i) - '0') + carry);
            str += (char)(sum % 10 + '0');
            carry = sum / 10;
        }

        // Add remaining carry
        if (carry > 0)
            str += (char)(carry + '0');

        // reverse resultant String
        String sum = new StringBuilder(str).reverse().toString();
        return new MyBigInteger(sum);
    }

    MyBigInteger times(MyBigInteger x) { //"this" will be str1, arg x will be str2

        int len1 = this.value.length();
        int len2 = x.value.length();
        if (len1 == 0 || len2 == 0){
            return new MyBigInteger();  //call default constructor with 0 value
        }

        // will keep the result number in vector
        // in reverse order
        int result[] = new int[len1 + len2];

        // Below two indexes are used to
        // find positions in result.
        int i_n1 = 0;
        int i_n2 = 0;

        // Go from right to left in num1
        for (int i = len1 - 1; i >= 0; i--)
        {
            int carry = 0;
            int n1 = this.value.charAt(i) - '0';

            // To shift position to left after every
            // multiplication of a digit in num2
            i_n2 = 0;

            // Go from right to left in num2
            for (int j = len2 - 1; j >= 0; j--)
            {
                // Take current digit of second number
                int n2 = x.value.charAt(j) - '0';

                // Multiply with current digit of first number
                // and add result to previously stored result
                // charAt current position.
                int sum = n1 * n2 + result[i_n1 + i_n2] + carry;

                // Carry
                carry = sum / 10;

                // Store result
                result[i_n1 + i_n2] = sum % 10;

                i_n2++;
            }

            // store carry in next cell
            if (carry > 0)
                result[i_n1 + i_n2] += carry;

            // To shift position to left after every
            // multiplication of a digit in num1.
            i_n1++;
        }

        // ignore '0's from the right
        int i = result.length - 1;
        while (i >= 0 && result[i] == 0) {
            i--;
        }

        // If all were '0's - means either both
        // or one of num1 or num2 were '0'
        if (i == -1) {
            return new MyBigInteger();
        }

        // generate the result String
        String s = "";

        while (i >= 0) {
            s += (result[i--]);
        }

        return new MyBigInteger(s);
    }

    @Override
    public String toString() {
        return value;
    }

    public static MyBigInteger fibLoopBig(int x) {
        MyBigInteger intA = new MyBigInteger("0");
        MyBigInteger intB = new MyBigInteger("1");
        MyBigInteger intC = new MyBigInteger("0");

        if(x == 0){
            return new MyBigInteger("0");
        }

        for(int i=2; i<=x; i++){
            intC = intA.plus(intB);
            intA = intB;
            intB = intC;
        }
        return intB;
    }

    public static void matrixPowerBig(MyBigInteger F[][], long x) {
        if(x==0 || x==1){
            return;
        }

        MyBigInteger val5 = new MyBigInteger("1");
        MyBigInteger val6 = new MyBigInteger("1");
        MyBigInteger val7 = new MyBigInteger("1");
        MyBigInteger val8 = new MyBigInteger("0");

        MyBigInteger M[][] = new MyBigInteger[][]{{val5,val6},{val7,val8}};

        matrixPowerBig(F, x/2);
        matrixMultiplyBig(F, F);

        if(x%2 != 0){
            matrixMultiplyBig(F, M);
        }
    }

    public static void matrixMultiplyBig(MyBigInteger F[][], MyBigInteger M[][]) {
        MyBigInteger vala = (F[0][0].times(M[0][0])).plus(F[0][1].times(M[1][0]));
        MyBigInteger valb = (F[0][0].times(M[0][1])).plus(F[0][1].times(M[1][1]));
        MyBigInteger valc = (F[1][0].times(M[0][0])).plus(F[1][1].times(M[1][0]));
        MyBigInteger vald = (F[1][0].times(M[0][1])).plus(F[1][1].times(M[1][1]));

        F[0][0] = vala;
        F[0][1] = valb;
        F[1][0] = valc;
        F[1][1] = vald;
    }

    public static MyBigInteger fibMatrixBig(long x) {
        MyBigInteger val1 = new MyBigInteger("1");
        MyBigInteger val2 = new MyBigInteger("1");
        MyBigInteger val3 = new MyBigInteger("1");
        MyBigInteger val4 = new MyBigInteger("0");

        MyBigInteger F[][] = new MyBigInteger[][]{{val1,val2},{val3,val4}};

        if(x==0){
            return new MyBigInteger("0");
        }

        matrixPowerBig(F, x-1);

        return F[0][0];
    }

    public static double fibFormula(double x) {
        double phi = (1 + Math.sqrt(5)) / 2;
        return (double) Math.round(Math.pow(phi, x) / Math.sqrt(5));
    }

    public static void main(String[] args)
    {
        /*
        MyBigInteger int1 = new MyBigInteger("100000000000000000000" );
        MyBigInteger int2 = new MyBigInteger("10");
        MyBigInteger int3 = new MyBigInteger("3333333333333333338888888888" );
        MyBigInteger int4 = new MyBigInteger("33333333" );
        MyBigInteger int5 = new MyBigInteger("9999999999999999999999999999");
        MyBigInteger int6 = new MyBigInteger("123123123123123123123123123123" );
        MyBigInteger int7 = new MyBigInteger("456456456456456456456" );
        MyBigInteger int8 = new MyBigInteger("17" );
        MyBigInteger int9 = new MyBigInteger("0" );
        MyBigInteger int10 = new MyBigInteger("1" );
        MyBigInteger int11 = new MyBigInteger("5000000000000" );

        MyBigInteger intX = int1.plus(int2);
        MyBigInteger intA = int3.plus(int4);
        MyBigInteger intB = int10.plus(int5);
        MyBigInteger intC = int6.plus(int7);
        MyBigInteger intD = int1.times(int9);
        MyBigInteger intE = int1.times(int2);
        MyBigInteger intF = int10.plus(int1);
        MyBigInteger intG = int4.times(int2);
        MyBigInteger intH = int11.plus(int11);
        MyBigInteger intI = int1.times(int11);

        System.out.println(intX.toString());
        System.out.println(intA.toString());
        System.out.println(intB.toString());
        System.out.println(intC.toString());
        System.out.println(intD.toString());
        System.out.println(intE.toString());
        System.out.println(intF.toString());
        System.out.println(intG.toString());
        System.out.println(intH.toString());

         */

        //MyBigInteger bigFibNum = fibLoopBig(150);
        //System.out.println("Testing fibLoopBig, fib(150) =  " + bigFibNum.toString());

        //MyBigInteger bigFibNum2 = fibMatrixBig(150);
        //System.out.println("Testing fibMatrixBig, fib(150) =  " + bigFibNum2.toString());

        System.out.printf("fibFormula(70) = %.9f\n", fibFormula(70));
        System.out.printf("fibFormula(71) = %.9f\n", fibFormula(71));
        System.out.printf("fibFormula(72) = %.9f\n", fibFormula(72));
        System.out.printf("fibFormula(73) = %.9f\n", fibFormula(73));
        System.out.printf("fibFormula(74) = %.9f\n", fibFormula(74));
        System.out.printf("fibFormula(75) = %.9f\n", fibFormula(75));
        System.out.printf("fibFormula(76) = %.9f\n", fibFormula(76));
        System.out.printf("fibFormula(77) = %.9f\n", fibFormula(77));
        System.out.printf("fibFormula(78) = %.9f\n", fibFormula(78));

    }
}