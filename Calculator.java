package prog04;

import java.util.Stack;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import prog02.UserInterface;
import prog02.GUI;
import prog02.ConsoleUI;

public class Calculator {
  static final String OPERATORS = "()+-*/u^";
  static final int[] PRECEDENCE = { -1, -1, 1, 1, 2, 2, 3, 4 };
  Stack<Character> operatorStack = new Stack<Character>();
  Stack<Double> numberStack = new Stack<Double>();
  UserInterface ui = new GUI("Calculator");
  boolean previousTokenWasNumberOrRightParenthesis = false;

  Calculator (UserInterface ui) { this.ui = ui; }

  void emptyStacks () {
    while (!numberStack.empty())
      numberStack.pop();
    while (!operatorStack.empty())
      operatorStack.pop();
  }
  void setPrevTokenToFalse() {
    previousTokenWasNumberOrRightParenthesis = false;
  }

  String numberStackToString () {
    String s = "numberStack: ";
    Stack<Double> helperStack = new Stack<Double>();
    // EXERCISE
    // Put every element of numberStack into helperStack
    // You will need to use a loop.  What kind?
    // What condition? When can you stop moving elements out of numberStack?
    // What method do you use to take an element out of numberStack?
    // What method do you use to put that element into helperStack?
    while (!numberStack.empty()) {
      double helperStackDouble = numberStack.pop();
      helperStack.push(helperStackDouble);
    }



    // Now put everything back, but also add each one to s:
    // s = s + " " + number;
while (!helperStack.empty()) {
  s +=  " " + helperStack.peek();
  double numberStackDouble = helperStack.pop();
  numberStack.push(numberStackDouble);
}


    return s;
  }

  String operatorStackToString () {
    String s = "operatorStack: ";
    Stack<Character> helperStack = new Stack<Character>();
    // EXERCISE
    while (!operatorStack.empty()) {
      char helperStackChar = operatorStack.pop();
      helperStack.push(helperStackChar);
    }

    while (!helperStack.empty()) {
    s += " " + helperStack.peek();
    char operatorStackChar = helperStack.pop();
    operatorStack.push(operatorStackChar);
  }


    return s;
  }

  void displayStacks () {
    ui.sendMessage(numberStackToString() + "\n" +
                   operatorStackToString());
  }

  void doNumber (double x) {
    previousTokenWasNumberOrRightParenthesis = true;
    numberStack.push(x);
    displayStacks();
  }

  void doOperator (char op) {
    if (op == '-' && !previousTokenWasNumberOrRightParenthesis) {
      processOperator('u');
    } else {
      previousTokenWasNumberOrRightParenthesis = false;
      processOperator(op);
    }
    displayStacks();
  }
  double doEquals () {
    while (!operatorStack.empty())
      evaluateTopOperator();

    return numberStack.pop();
  }
    
  double evaluateOperator (double a, char op, double b) {
    switch (op) {
      case '+':
        return a + b;
      // EXERCISE
      case '-':
        return a - b;
      case '*':
        return a * b;
      case '/':
        return a / b;
      case '^':
        return Math.pow(a, b);
    }
    System.out.println("Unknown operator " + op);
    return 0;
  }

  void evaluateTopOperator () {
    if (operatorStack.peek() == 'u') {
      operatorStack.pop();
      double num1 = numberStack.pop();
      num1 = num1 * -1;
      numberStack.push(num1);
      displayStacks();
    } else {
      char op = operatorStack.pop();
      double num1 = numberStack.pop();
      double num2 = numberStack.pop();
      numberStack.push(evaluateOperator(num2, op, num1));
      // EXERCISE
      displayStacks();
    }
  }

  void processOperator (char op) {

    if (op == '(' || op == 'u') {
      operatorStack.push(op);
    }

    else if(op == ')') {
      previousTokenWasNumberOrRightParenthesis = true;
      while (operatorStack.peek() != '(') {
        evaluateTopOperator();
      }
      operatorStack.pop();
    }

    else if (operatorStack.empty()) {
      operatorStack.push(op);
    }

    else {
      int operatorInt = precedence(operatorStack.peek());
      int precedenceInt = precedence(op);
     while (operatorInt >= precedenceInt) {
       evaluateTopOperator();
       if (operatorStack.empty()) {
         break;
       } else {
         operatorInt = precedence(operatorStack.peek());
       }
     }
        operatorStack.push(op);
      }
    }




  
  static boolean checkTokens (UserInterface ui, Object[] tokens) {
      for (Object token : tokens)
        if (token instanceof Character &&
            OPERATORS.indexOf((Character) token) == -1) {
          ui.sendMessage(token + " is not a valid operator.");
          return false;
        }
      return true;
  }

  static void processExpressions (UserInterface ui, Calculator calculator) {
    while (true) {
      String line = ui.getInfo("Enter arithmetic expression or cancel.");
      if (line == null)
        return;
      Object[] tokens = Tokenizer.tokenize(line);
      if (!checkTokens(ui, tokens))
        continue;
      try {
        for (Object token : tokens) {
          if (token instanceof Double)
            calculator.doNumber((Double) token);
          else
            calculator.doOperator((Character) token);
        }
        double result = calculator.doEquals();
        ui.sendMessage(line + " = " + result);
        calculator.setPrevTokenToFalse();
      } catch (Exception e) {
        ui.sendMessage("Bad expression.");
        calculator.emptyStacks();
      }
    }
  }
  public int precedence(char op) {
    switch(op) {
      case('('): case (')') :
        return (-1);
      case('+'): case('-'):
        return(1);
      case('*'): case('/'):
        return(2);
      case('u'):
        return(3);
      case('^'):
        return(4);
    }
    System.out.println("Unknown operator " + op);
    return 0;
  }

  public static void main (String[] args) {
    UserInterface ui = new ConsoleUI();
    Calculator calculator = new Calculator(ui);
    processExpressions(ui, calculator);
  }
}
