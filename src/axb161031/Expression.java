/**
 * The Shunting Yard algorithm program implements an application that
 * takes the numbers and operations as input and displays its corresponding infix and expression tree.
 *
 *
 * @author  Abinash Bhattarai
 * @netid   axb161031
 * @version 1.0
 * @since   2018-9-28
 */
package axb161031;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

/** Class to store a node of expression tree
 For each internal node, element contains a binary operator
 List of operators: +|*|-|/|%|^ 
 Other tokens: (|)
 Each leaf node contains an operand (long integer)
 */

public class Expression {
    public enum TokenType {  // NIL is a special token that can be used to mark bottom of stack
        PLUS, TIMES, MINUS, DIV, MOD, POWER, OPEN, CLOSE, NIL, NUMBER
    }

    public static class Token {
        TokenType token;
        int priority; // for precedence of operator
        Long number;  // used to store number of token = NUMBER
        String string;

        Token(TokenType op, int pri, String tok) {
            token = op;
            priority = pri;
            number = null;
            string = tok;
        }

        // Constructor for number.  To be called when other options have been exhausted.
        Token(String tok) {
            token = TokenType.NUMBER;
            number = Long.parseLong(tok);
            string = tok;
        }

        boolean isOperand() { return token == TokenType.NUMBER; }

        public long getValue() {
            return isOperand() ? number : 0;
        }

        public String toString() { return string; }
    }

    Token element;
    Expression left, right;


    /*
    * This method is used to create token corresponding to a string.
    * It takes String tok as an argument
    * tok is "+" | "*" | "-" | "/" | "%" | "^" | "(" | ")"| NUMBER
    *  NUMBER is either "0" or "[-]?[1-9][0-9]*
    *  @param tok is: String parameter of the method
    *  @return Token: Token class with priority of token.
    */
    static Token getToken(String tok) {  //
        Token result;
        switch(tok) {
            case "^":
                result = new Token(TokenType.POWER, 4, tok);
                break;
            case "*":
                result = new Token(TokenType.TIMES, 3, tok);
                break;
            case "/":
                result = new Token(TokenType.DIV, 3, tok);
                break;
            case "%":
                result = new Token(TokenType.MOD, 3, tok);
                break;
            case "+":
                result = new Token(TokenType.PLUS, 2, tok);
                break;
            case "-":
                result = new Token(TokenType.MINUS, 2, tok);
                break;
            case "(":
                result = new Token(TokenType.OPEN, 1, tok);
                break;

            case ")":
                result = new Token(TokenType.CLOSE, 1, tok);
                break;


            // Complete rest of this method
            default:
                result = new Token(tok);
                break;
        }
        return result;
    }

    private Expression() {
        element = null;
    }

    private Expression(Token oper, Expression left, Expression right) {
        this.element = oper;
        this.left = left;
        this.right = right;
    }

    private Expression(Token num) {
        this.element = num;
        this.left = null;
        this.right = null;
    }


  /*
    * This method is used to return expression tree of corresponding infix expression.
    * Example of expression = 1 * 2 + ( 4 / 5 ) ^ 2
    * Based on Shunting yard algorithm
    *  @param: List of tokens
    *  @return: Expression Tree
    */
    //


    public static Expression infixToExpression(List<Token> exp) {
        Iterator<Token> itr = exp.iterator();
        //Expression tree = new Expression();
        Stack<Expression> elements = new Stack<>();
        Stack<Token> operatorStack = new Stack<>();
        Token ex;

        while (itr.hasNext()) {
            ex = itr.next();
            if (ex.isOperand()) {
                Expression tree= new Expression(ex);
                elements.push(tree);
            }
            //opening parenthesis
            else if (ex.toString().equals("(")) {
            operatorStack.push(ex);
            }
            //closing parenthesis
            else if(ex.toString().equals(")")) {
                while ( !operatorStack.peek().toString().equals("(")  ) {
                    Expression ex1 = elements.pop();
                    Expression ex2 = elements.pop();
                    Token operator = operatorStack.pop();
                    elements.push(new Expression(operator, ex2, ex1));

                }
                operatorStack.pop();


            }
            //operator
            else if( operatorStack.empty() || ex.priority > operatorStack.peek().priority  ){

                operatorStack.push(ex);


                }
                else{

                while(!operatorStack.empty() && ex.priority <= operatorStack.peek().priority)
                {
                    Expression exp1=elements.pop();
                    Expression exp2=elements.pop();
                    Token operator= operatorStack.pop();
                    elements.push(new Expression(operator,exp2,exp1));
                }

                operatorStack.push(ex);
            }






        }
        while(!operatorStack.empty()){
            Expression exp1=elements.pop();
            Expression exp2=elements.pop();
            Token operator=operatorStack.pop();
            elements.push(new Expression(operator,exp2,exp1));
        }
        return elements.pop();
    }


    /*
   * This method is used to convert infix expression to postfix.
   * Example of infix expression = 1 * 2 + ( 4 / 5 ) ^ 2
   * Example of postfix expression=1 2 * 4 5 / 2 ^ +
   * Based on Shunting yard algorithm
   *  @param: List of tokens
   *  @return: List of postfix expression
   */
    //
    public static List<Token> infixToPostfix(List<Token> exp) {  // To do
        Stack<Token> operatorStack= new Stack<>();
        List<Token> outputQueue=new LinkedList<>();
        Iterator<Token> it=exp.iterator();
        Token ex;
        while(it.hasNext()) {
            ex = it.next();
            if (ex.isOperand()) {
                outputQueue.add((ex));

            } else if (ex.toString().equals("(")) {
                operatorStack.push(ex);
            } else if (ex.toString().equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().toString().equals("(")) {
                    outputQueue.add(operatorStack.pop());
                }
                operatorStack.pop();

            }
            // Operator
            else {


                while (!operatorStack.isEmpty() && operatorStack.peek().priority >= ex.priority) {
                    outputQueue.add(operatorStack.pop());
                }

                operatorStack.push(ex);
                }
        }

        while(!operatorStack.empty()){
            outputQueue.add(operatorStack.pop());
        }



        return outputQueue;
    }



    // Given a postfix expression, evaluate it and return its value.
    public static long evaluatePostfix(List<Token> exp) {
        Stack<Long> values=new Stack<>();
        Iterator<Token> it=exp.iterator();
        Token ex;
        while(it.hasNext()){
            ex=it.next();
            if(ex.isOperand()){
                values.push(ex.getValue());
            }
            else{

                long value1=values.pop();
                long value2=values.pop();
                if(Objects.equals(ex.toString(), "+")){
                    values.push(value1+value2);
                }
                else if(Objects.equals(ex.toString(), "-")){
                    values.push(value2-value1);
                }
                else if(Objects.equals(ex.toString(), "*")){
                    values.push(value2*value1);
                }
                else if(Objects.equals(ex.toString(), "/")) {
                    values.push(value2 / value1);
                }
                else if(Objects.equals(ex.toString(), "%")){
                    values.push(value2%value1);
                    }
                else if(Objects.equals(ex.toString(), "^")){
                    values.push((long)Math.pow((double)value2,(double)value1));
                }
            }


        }

        return values.pop();
    }

    /*
  * This method is used to evaluate expression tree.
  *  @param:Expression tree
  *  @return: Long value : Evaluation and calculation of expression tree
  *
  */
    //
    public static long evaluateExpression(Expression tree)
    {
        if(tree.left==null && tree.right==null) {
            //System.out.println(tree.element);
            //System.out.println(tree.element.getValue());
            return tree.element.getValue();
        }
            else{
                long left=evaluateExpression(tree.left);
                long right=evaluateExpression(tree.right);

                if(Objects.equals(tree.element.toString(), "+")){
                   return  left = left+right;
                }
                else if(Objects.equals(tree.element.toString(), "-")){
                    return  left = left-right;
                }
                else if(Objects.equals(tree.element.toString(), "*")){
                    return  left = left*right;
                }
                else if(Objects.equals(tree.element.toString(), "/")) {
                    return  left = left/right;
                }
                else if(Objects.equals(tree.element.toString(), "%")){
                    return left = left%right;
                }
                else {
                    return  left = (long)Math.pow(left,right);
                }


            }


    }

    // sample main program for testing
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
       // System.out.println("Hello world");
        if (args.length > 0) {
            File inputFile = new File("inputfile.txt");
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }

        int count = 0;
        while(in.hasNext()) {
            String s = in.nextLine();
            List<Token> infix = new LinkedList<>();
            Scanner sscan = new Scanner(s);
            int len = 0;
            while(sscan.hasNext()) {
                infix.add(getToken(sscan.next()));
                len++;
            }
            if(len > 0) {
                count++;
                System.out.println("Expression number: " + count);
                System.out.println("Infix expression: " + infix);
                Expression exp = infixToExpression(infix);
                List<Token> post = infixToPostfix(infix);
                System.out.println("Postfix expression: " + post);
                long pval = evaluatePostfix(post);
                long eval = evaluateExpression(exp);
                System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
            }
        }
    }
}
