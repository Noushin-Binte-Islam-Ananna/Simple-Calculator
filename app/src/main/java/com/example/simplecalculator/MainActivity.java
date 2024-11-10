package com.example.simplecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private StringBuilder input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        input = new StringBuilder();
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        input.append(button.getText().toString());
        display.setText(input.toString());
    }

    public void onClearClick(View view) {
        input.setLength(0);
        display.setText("0");
    }

    public void onDeleteClick(View view) {
        if (input.length() > 0) {
            input.deleteCharAt(input.length() - 1);
            display.setText(input.length() > 0 ? input.toString() : "0");
        }
    }

    public void onEqualClick(View view) {
        try {
            double result = evaluateExpression(input.toString());
            display.setText(String.valueOf(result));
            input.setLength(0);
        } catch (Exception e) {
            display.setText("Error");
            input.setLength(0);
        }
    }

    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;

        while (i < expression.length()) {
            char ch = expression.charAt(i);

            // If it's a whitespace, skip it
            if (ch == ' ') {
                i++;
                continue;
            }

            // If it's a number, parse the whole number
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(sb.toString()));
                i--; // Adjust index because we've moved past the number
            }
            // If it's an opening parenthesis, push it to the operators stack
            else if (ch == '(') {
                operators.push(ch);
            }
            // If it's a closing parenthesis, solve the entire bracket
            else if (ch == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop(); // Pop the opening parenthesis
            }
            // If it's an operator, process it
            else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && precedence(ch) <= precedence(operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
            i++;
        }

        // Apply remaining operators to remaining numbers
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        // The final result is the last number left in the numbers stack
        return numbers.pop();
    }

    // Helper method to define operator precedence
    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    // Helper method to perform arithmetic operations
    private double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            default: return 0;
        }
    }
}
