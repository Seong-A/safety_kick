package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pg);

        Intent intent = getIntent();
        double paymentAmount = intent.getDoubleExtra("PAYMENT_AMOUNT", 0);
        String paymentDate = intent.getStringExtra("PAYMENT_DATE");
        String cardName = intent.getStringExtra("CARD_NAME");

        TextView paymentAmountTextView = findViewById(R.id.money);
        TextView paymentDateTextView = findViewById(R.id.pay_date);
        TextView cardNameTextView = findViewById(R.id.card_name);

        paymentAmountTextView.setText(paymentAmount + "원");
        paymentDateTextView.setText(paymentDate);
        cardNameTextView.setText(cardName);

        findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentListActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });
    }
}
