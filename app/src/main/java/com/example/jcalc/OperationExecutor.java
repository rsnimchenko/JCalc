package com.example.jcalc;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class OperationExecutor {
    private final int ENTER_FIRST_OPERAND = 0;
    private final int ENTER_OPERATION = 1;
    private final int ENTER_SECOND_OPERAND = 2;
    private final int ENTER_AFTER_RESULT = 3;
    private int currentStatus = ENTER_FIRST_OPERAND;
    //\u221a знак квадратного корня
    private final List<String> operations = Arrays.asList("-", "+", "*", "/", "^", "\u221a");

    private String result = "";
    private String firstOperand = "";
    private String secondOperand = "";
    private String operation = "";

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setFirstOperand(String firstOperand) {
        this.firstOperand = firstOperand;
    }

    public String getFirstOperand() {
        return firstOperand;
    }

    public void setSecondOperand(String secondOperand) {
        this.secondOperand = secondOperand;
    }

    public String getSecondOperand() {
        return secondOperand;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void enterNumber(String num) {
        if(currentStatus == ENTER_FIRST_OPERAND) {
            firstOperand = checkOperand(firstOperand, num);
        } else if(currentStatus == ENTER_SECOND_OPERAND || currentStatus == ENTER_OPERATION) {
            secondOperand = checkOperand(secondOperand, num);
        } else if(currentStatus == ENTER_AFTER_RESULT) {
            firstOperand = "";
            currentStatus = ENTER_FIRST_OPERAND;
            enterNumber(num);
        }
    }

    public void enterOperation(String op) {
        /*
        Возможность ввести отрицательное число
        При вводе нескольких минусов. последующие игнорируются
         */
        if(currentStatus == ENTER_FIRST_OPERAND) {
            if(firstOperand.isEmpty()) {
                if(op.equals("-")) firstOperand = op;
                if(op.equals("/")) {
                    firstOperand = "2";
                    operation = "\u221a";
                    currentStatus = ENTER_SECOND_OPERAND;
                }
            } else {
                if(firstOperand.equals("-") && operations.contains(op)) return;
                operation = op;
                currentStatus = ENTER_SECOND_OPERAND;
            }
        } else if(currentStatus == ENTER_SECOND_OPERAND) {
            if(secondOperand.isEmpty()) {
                if(op.equals("-")) secondOperand = op;
                else if(operation.equals("*") && op.equals("*")) operation = "^";
                else if(operation.equals("/") && op.equals("/")) operation = "\u221a";
            } else {
                if(secondOperand.equals("-") && operations.contains(op)) return;
            }
        } else if(currentStatus == ENTER_AFTER_RESULT) {
            operation = op;
            currentStatus = ENTER_SECOND_OPERAND;
        }
    }

    private String checkOperand(String operand, String num) {
        /*
        Если операнд пустой, или отрицательный, то при введении "." заменять на "0."
        Если вводится подряд несколько нулей, то игнорировать последующие нули
         */
        String oper = operand;
        if(operand.isEmpty() || operand.equals("-")){
            if(num.equals(".")) {
                oper += "0.";
            } else {
                oper += num;
            }
        } else {
            if((operand.equals("0") || operand.equals("-0")) && !num.equals(".")) return operand;
            else if(operand.contains(".") && num.equals(".")) return operand;
            else oper += num;
        }
        return oper;
    }

    public String execute() {
        Double firstOper = Double.valueOf(firstOperand);
        Double secondOper = Double.valueOf(secondOperand);
        switch (operation) {
            case "+":
                result = Double.toString(firstOper + secondOper);
                break;
            case "-":
                result = Double.toString(firstOper - secondOper);
                break;
            case "*":
                result = Double.toString(firstOper * secondOper);
                break;
            case "/":
                result = Double.toString(firstOper / secondOper);
                break;
            case "^":
                result = Double.toString(Math.pow(firstOper, secondOper));
                break;
            case "\u221a":
                result = Double.toString(Math.pow(secondOper, 1/firstOper));
                break;
        }
        checkResultText();
        firstOperand = result;
        secondOperand = "";
        operation = "";
        currentStatus = ENTER_AFTER_RESULT;
        return result;
    }

    public void checkResultText() {
        //Метод для откугления результата
        BigDecimal bd = new BigDecimal(result);
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        result = bd.toString();
        String fraction = result.substring(result.indexOf(".") + 1);
        if (fraction.equals("000")) {
            result = result.substring(0, result.indexOf("."));
        } else {
            while(fraction.endsWith("0")) {
                fraction = fraction.substring(0, fraction.length() - 1);
            }
            result = result.substring(0, result.indexOf(".") + 1) + fraction;
        }
    }

    @Override
    public String toString() {
        return firstOperand + ' ' + operation + ' ' + secondOperand;
    }

    public void clearData() {
        firstOperand = "";
        secondOperand = "";
        operation = "";
        result = "";
        currentStatus = ENTER_FIRST_OPERAND;
    }

    public void deleteSymbol() {
        if(currentStatus == ENTER_FIRST_OPERAND || currentStatus == ENTER_AFTER_RESULT) {
            if(!firstOperand.isEmpty())
                firstOperand = firstOperand.substring(0, firstOperand.length() - 1);
            currentStatus = ENTER_FIRST_OPERAND;
        } else if(currentStatus == ENTER_OPERATION) {
            operation = "";
            currentStatus = ENTER_FIRST_OPERAND;
        } else if(currentStatus == ENTER_SECOND_OPERAND) {
            if(!secondOperand.isEmpty())
                secondOperand = secondOperand.substring(0, secondOperand.length() - 1);
            else {
                currentStatus = ENTER_OPERATION;
                deleteSymbol();
            }
        }
    }
}
