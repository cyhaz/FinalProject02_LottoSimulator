package kr.co.youhyun.finalproject02_lottosimulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.youhyun.finalproject02_lottosimulator.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    List<TextView> winNumTxtList = new ArrayList<>();
    int[] winLottoNumArr = new int[6];
    int bonusNum = 0;

    int[] myLottoNumArr = {3,11,17,29,35,38};

    long useMoneyAmount = 0;   // 로또구매에 사용한 총금액
    long winMoneyAmount = 0;   // 총 당첨금액

    int firstRankCount = 0;
    int secondRankCount = 0;
    int thirdRankCount = 0;
    int fourthRankCount = 0;
    int fifthRankCount = 0;
    int nonRankCount = 0;


    ActivityMainBinding binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupEvents();
        setValues();
    }

    @Override
    public void setupEvents() {

        binding.buyOneLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                당첨번호 생성 => 텍스트뷰에 반영
                makeWinLottoNum();

//                몇등인지 판단하는 함수
                checkLottoRank();
            }
        });

    }

    @Override
    public void setValues() {

        winNumTxtList.add((binding.winLottoNumTxt01));
        winNumTxtList.add((binding.winLottoNumTxt02));
        winNumTxtList.add((binding.winLottoNumTxt03));
        winNumTxtList.add((binding.winLottoNumTxt04));
        winNumTxtList.add((binding.winLottoNumTxt05));
        winNumTxtList.add((binding.winLottoNumTxt06));

    }

    void makeWinLottoNum() {
//        6개의 숫자(배열) + 보너스번호 1개(int변수)
//        => 이 함수에서만이 아니라, 다른곳에서도 쓸 예정 (당첨등수 확인 때도 사용)
//        => 배열과 변수는 멤버변수로 생성!!

//        당첨번호 + 보너스번호를 모두 0으로 초기화
//        (이미 뽑은 번호가 있다면 모두 날리자! -> 초기화)
        for (int i=0; i<winLottoNumArr.length; i++) {
            winLottoNumArr[i] = 0;
        }
        bonusNum = 0;

//        로또번호 6개 생성
//        1) 1~45 사이의 숫자
//        2) 중복 허용 X
        for (int i=0; i<winLottoNumArr.length; i++) {
//            1~45의 숫자 뽑고, 중복이 아니면 당첨번호로 선정. (중복이 아닐때까지 계속 뽑쟈)
            while (true) {
                int randomNum = (int) (Math.random()*45+1);
                boolean isDuplOk = true;
                for (int winNum : winLottoNumArr) {
                    if (winNum == randomNum) {
                        isDuplOk = false;
                        break;
                    }
                }
                if (isDuplOk) {
                    winLottoNumArr[i] = randomNum;
                    Log.i("당첨번호", randomNum+"");
                    break;
                }
            }
        }
//        6개의 당첨번호를 작은 숫자부터 정렬
        Arrays.sort(winLottoNumArr);
        for (int i=0; i<winLottoNumArr.length; i++) {
            winNumTxtList.get(i).setText(winLottoNumArr[i]+"");   // +"" 붙이면 강제로 문자로 변경

        }

//        보너스번호 생성 (1~45 / 당첨번호와 중복 X)
        while (true) {
            int randomNum = (int) (Math.random()*45+1);
            boolean isDuplOk = true;
            for (int winNum : winLottoNumArr) {
                if (winNum == randomNum) {
                    isDuplOk = false;
                    break;
                }
            }
            if (isDuplOk) {
                bonusNum = randomNum;
                break;
            }
        }

//        보너스번호 생성됨!
        binding.bonusNumTxt.setText(bonusNum+"");

    }

    void checkLottoRank() {
        useMoneyAmount += 1000;
        binding.useMoneyTxt.setText(String.format("사용금액 : %,d원", useMoneyAmount));

//        내 번호를 하나 들고 => 당첨번호 6개를 계속 돌며 반복 => 몇개의 숫자를 맞췄는가?
        // 맞춘갯수 담아줄 변수
        int correctCount = 0;

        for (int myNum : myLottoNumArr) {
            for (int winNum : winLottoNumArr) {
                if (myNum == winNum) {
                    correctCount++;
                }
            }
        }

//        correctCount의 값에 따라 등수를 판정
        if (correctCount == 6) {
            // 1등
            winMoneyAmount += 1600000000;
            firstRankCount++;

        } else if (correctCount == 5) {
            // 2등? 3등? 재검사 => 보너스번호 맞췄나?
            // 내 번호중에 보너스번호와 같은게 있나?
            boolean hasBonusNum = false;
            for (int myNum : myLottoNumArr) {
                if (myNum == bonusNum) {
                    hasBonusNum = true;
                    break;
                }
            }
            if (hasBonusNum) {
                winMoneyAmount += 75000000;
                secondRankCount++;
            } else {
                winMoneyAmount += 1500000;
                thirdRankCount++;
            }

        } else if (correctCount == 4) {
            // 4등
            winMoneyAmount += 50000;
            fourthRankCount++;
        } else if (correctCount == 3) {
            // 5등
            useMoneyAmount -= 5000;
            fifthRankCount++;
        } else {
            // 꽝 -> 아무일도...
            nonRankCount++;
        }

//        당첨금액 텍스트에도 반영
        binding.winMoneyTxt.setText(String.format("당첨금액 : %,d원", winMoneyAmount));

//        당첨횟수들도 텍스트뷰에 반영
        binding.firstRankCountTxt.setText(String.format("1등 : %,d회", firstRankCount));
        binding.secondtRankCountTxt.setText(String.format("2등 : %,d회", secondRankCount));
        binding.thirdRankCountTxt.setText(String.format("3등 : %,d회", thirdRankCount));
        binding.fourthRankCountTxt.setText(String.format("4등 : %,d회", fourthRankCount));
        binding.fifthRankCountTxt.setText(String.format("5등 : %,d회", fifthRankCount));
        binding.nonRankCountTxt.setText(String.format("꽝 : %,d회", nonRankCount));
    }

}
