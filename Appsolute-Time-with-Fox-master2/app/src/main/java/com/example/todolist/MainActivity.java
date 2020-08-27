package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.realm.Realm;

// TODO: gamify 관련해서 필요한 코드 작성

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener{
    ActivityMainBinding binding;

    FloatingActionButton btnAdd;
    Button btnDDay;
    Switch switchShowDDay;
    Button btnOasis;
    ConstraintLayout layoutBackground;

    DDayFragment d_dayFrag;
    ToDoFragment todoFrag;
    OasisFragment oasisFrag;

    FragmentManager fm;
    FragmentTransaction ftran;

    boolean isDDay = false;
    boolean isOasis = false;
    int currList = 0;
    float pointX; float pointY; float oasisNum;

    private long backKeyPressed = 0;



    Button insertButton;
    EditText todoEdit;
    private ArrayList<Todo> todoArrayList;
    private TodoAdapter todoAdapter;        // 어뎁터를 사용하기 위해 정의




    private ListView listView;                      // 리스트뷰
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private List<String> list;                      // String 데이터를 담고있는 리스트
    private ListViewAdapter adapter;                // 리스트뷰의 아답터
    private int page = 0;                           // 페이징변수. 초기 값은 0 이다.
    private final int OFFSET = 20;                  // 한 페이지마다 로드할 데이터 갯수.
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수



    // Use Realm DB
    Realm realm;

    // Gamify 관련 변수
    public static Context mcontext;
    SharedPreferences Log;
    SharedPreferences.Editor editor;
    SimpleDateFormat mFormat;
    ProgressBar prgbar;
    TextView percentV, levelView;

    int imageResources[] = {
            R.drawable.fox_swim, R.drawable.fox, R.drawable.fox_sleep, R.drawable.fox_red, R.drawable.fox_pilot,
            R.drawable.fox_angry,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mcontext = this;

        // Realm DB 사용을 위한 초기화
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();

        layoutBackground = binding.layoutBackground;

        // TODO: 사막 background로 설정
        layoutBackground.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.dday_background));


        btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: 할 일 추가 화면 여기서 띄우기
            }
        });

        btnDDay = binding.btnDDay;
        btnDDay.setText("To D-Day List");
        isDDay = false;
        btnDDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!isDDay){
                    setFragment(1);
                    currList = 1;
                    btnDDay.setText("To to do list");
                    if(isOasis) {
                        btnOasis.setText("Oasis");
                        btnOasis.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.airplane_oasis_go));
                        isOasis = false;
                        layoutBackground.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.dday_background));
                    }
                    isDDay = true;
                }
                else{
                    setFragment(0);
                    currList = 0;
                    btnDDay.setText("To D-Day List");
                    if(isOasis) {
                        btnOasis.setText("Oasis");
                        btnOasis.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.airplane_oasis_go));
                        isOasis = false;
                        layoutBackground.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.dday_background));
                    }
                    isDDay = false;
                }
            }
        });

        btnOasis = binding.btnOasis;
        btnOasis.setText("Oasis");
        isOasis = false;

        btnOasis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!isOasis){
                    // TODO: 오아시스 background로 전환
                    btnOasis.setText("Go Back");
                    btnOasis.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.airplane_oasis_back));
                    isOasis = true;
                    layoutBackground.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.oasis_background));

                    setFragment(2);
                }
                else{
                    // TODO: 사막 background로 전환
                    btnOasis.setText("Oasis");
                    btnOasis.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.airplane_oasis_go));
                    isOasis = false;
                    layoutBackground.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.dday_background));
                    setFragment(currList);
                }

            }
        });


// 시도 중
        @SuppressLint("WrongViewCast") RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listFragment);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        todoArrayList = new ArrayList<>();
        todoAdapter = new TodoAdapter(todoArrayList);       // 어뎁터 안에 ArrayList 넣기
        recyclerView.setAdapter(todoAdapter);   // 어뎁터를 셋팅

        insertButton = (Button) findViewById(R.id.btnAdd) ;


        insertButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Todo newTodo = new Todo(todoEdit.getText().toString());     // 입력한 문자열로 Todo 객체 생성
                todoArrayList.add(newTodo);     // 생성한 객체를 ArrayList<Todo> 타입의  TodoArrayList에 추가
                todoAdapter.notifyDataSetChanged();     // 어뎁터에게 데이터 셋이 변경되었음을 알린다.
                todoEdit.setText(null);
            }
        });

        setContentView(R.layout.fragment_d_day);

        listView = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        list = new ArrayList<String>();
        adapter = new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);


        listView.setOnScrollListener((AbsListView.OnScrollListener) this);
        getItem();
// 여기까지


        d_dayFrag = new DDayFragment();
        todoFrag = new ToDoFragment();
        oasisFrag = new OasisFragment();

        setFragment(0);
        currList = 0;

        // gamify 관련 코드
        final ImageView wordbox = binding.wordBox;
        final TextView hi = binding.hi;
        ImageButton fox = binding.fox;

        fox.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordbox.setVisibility(View.VISIBLE);
                hi.setVisibility(View.VISIBLE);

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()  {
                    public void run() {
                        wordbox.setVisibility(View.INVISIBLE);
                        hi.setVisibility(View.INVISIBLE);
                    }
                }, 3000);

            }
        });
        Log = getSharedPreferences("Log", MODE_PRIVATE);
        int currentFox = Log.getInt("Fox", imageResources[1]);
        fox.setBackground(ContextCompat.getDrawable(mcontext, currentFox));
        levelUp();

    }


    //  다시 시작
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
        // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
        // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
        // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            // 화면이 바닦에 닿을때 처리
            // 로딩중을 알리는 프로그레스바를 보인다.
            progressBar.setVisibility(View.VISIBLE);

            // 다음 데이터를 불러온다.
            getItem();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
        // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
        // totalItemCount : 리스트 전체의 총 갯수
        // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    private void getItem(){

        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
        mLockListView = true;

        // 다음 20개의 데이터를 불러와서 리스트에 저장한다.
        for(int i = 0; i < 20; i++){
            String label = "Label " + ((page * OFFSET) + i);
            list.add(label);
        }

        // 1초 뒤 프로그레스바를 감추고 데이터를 갱신하고, 중복 로딩 체크하는 Lock을 했던 mLockListView변수를 풀어준다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mLockListView = false;
            }
        },1000);
    }

    //여기까지


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        View view = findViewById(R.id.listFragment);
        pointX = view.getWidth();
        pointY = view.getHeight();
        Toast.makeText(getApplicationContext(),String.valueOf(pointX) + " " + String.valueOf(pointY),Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressed + 2000){
            backKeyPressed = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"back 버튼을 한 번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(System.currentTimeMillis() <= backKeyPressed + 2000){
            finish();
        }

    }

    // 0: to do list, 1: d-day list, 2: oasis
    public void setFragment(int n){
        fm = getSupportFragmentManager();
        ftran = fm.beginTransaction();

        switch(n){
            case 0:
                ftran.replace(R.id.listFragment, todoFrag);
                break;
            case 1:
                ftran.replace(R.id.listFragment, d_dayFrag);
                break;
            case 2:
                ftran.replace(R.id.listFragment, oasisFrag);
                break;
            default:
                break;
        }
        ftran.commit();
    }

    // TODO: 여기부터 gamify 관련 코드 작성
    public String getdate() {
        mFormat = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = new Date(System.currentTimeMillis());
        String date = mFormat.format(nowDate);
        return date;
    }
    public void levelView(int level) {
        levelView = binding.levelNum;
        levelView.setText(String.valueOf(level));
    }
    public void percentView(String percentS, int percentN) {
        prgbar = binding.loveprg;
        percentV = binding.percent;
        percentV.setText(percentS);
        prgbar.setProgress(percentN, true);
    }
    public void levelupView(int level) {
        levelView(level);
        final ImageView wordbox = binding.wordBox;
        final TextView hi = binding.hi;
        ImageButton fox = binding.fox;

        wordbox.setVisibility(View.VISIBLE);
        hi.setText("축하해!");
        hi.setVisibility(View.VISIBLE);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                hi.setText("안녕!"); //5초후 다시 안녕으로 바꾸고 말풍선 안보이게
                wordbox.setVisibility(View.INVISIBLE);
                hi.setVisibility(View.INVISIBLE);
            }
        }, 5000);
    }

    public int[] getLog() {
        Log = getSharedPreferences("Log", MODE_PRIVATE);
        int[] arr = new int[4];
        arr[0] = Log.getInt("Date", Integer.parseInt(getdate()));
        arr[1] = Log.getInt("Percent", 0);
        arr[2] = Log.getInt("Finish", 0);
        arr[3] = Log.getInt("Level", 0);
        return arr;
    }

    public void change_percent(int itemnum) {
        Log = getSharedPreferences("Log", MODE_PRIVATE);
        editor = Log.edit();

        int finishTask = Log.getInt("Finish", 0);
        int percent = (int)((float) finishTask/itemnum*100);
        editor.putInt("Percent", percent);
        editor.putInt("Date", Integer.parseInt(getdate()));
        editor.commit();
        percentView(String.valueOf(percent), percent);
    }

    public void taskDone(int itemnum) {
        Log = getSharedPreferences("Log", MODE_PRIVATE);
        editor = Log.edit();

        int finishTask = Log.getInt("Finish", 0);
        finishTask++;
        editor.putInt("Finish", finishTask);
        editor.commit();
    }

    public void newfox(ImageButton fox) {
        int randomImage = imageResources[new Random().nextInt(imageResources.length)];
        fox.setBackground(ContextCompat.getDrawable(mcontext, randomImage));
        editor.putInt("Fox", randomImage);
        editor.commit();
    }

    public Boolean[] getFoxLog() {
        Log = getSharedPreferences("Log", MODE_PRIVATE);
        Boolean[] arr = new Boolean[imageResources.length];
        arr[0] = Log.getBoolean("FSwim", false);
        arr[1] = Log.getBoolean("FNormal", false);
        arr[2] = Log.getBoolean("FSleep", false);
        arr[3] = Log.getBoolean("FRed", false);
        arr[4] = Log.getBoolean("FPilot", false);
        arr[5] = Log.getBoolean("FAngry", false);
        return arr;
    }

    public void setFoxLog(int i) {
        Log = getSharedPreferences("Log", MODE_PRIVATE);
        editor = Log.edit();
        if(i==0) editor.putBoolean("FSwim", true);
        if(i==1) editor.putBoolean("FNormal", true);
        if(i==2) editor.putBoolean("FSleep", true);
        if(i==3) editor.putBoolean("FRed", true);
        if(i==4) editor.putBoolean("FPilot", true);
        if(i==5) editor.putBoolean("FAngry", true);
        editor.commit();
    }

    public void levelUp() {
        int[] arr = getLog();
        String dateLog = String.valueOf(arr[0]);
        String date = getdate(); int dateN = Integer.parseInt(date);
        Log = getSharedPreferences("Log", MODE_PRIVATE);
        editor = Log.edit();

        mFormat = new SimpleDateFormat("EE", Locale.KOREAN);
        Date nowDate = new Date(System.currentTimeMillis());
        String weekday = mFormat.format(nowDate);
        ImageButton fox = binding.fox;

        if((dateN >= arr[0] + 1)||(Integer.parseInt(date.substring(4,5)) > Integer.parseInt(dateLog.substring(4,5)))) {
            if(arr[1]>=70) {
                if (arr[3] < 7) {
                    int levelDiff = arr[3]++;
                    editor.putInt("Level", levelDiff);
                    levelupView(levelDiff);
                } else Toast.makeText(this, "이미 길들이기 LV.7달성!", Toast.LENGTH_LONG).show();
                levelView(arr[3]);
            }
            else if(arr[1]<70) {
                Toast.makeText(this, "70%달성 실패", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "어제 달성률: "+ arr[1], Toast.LENGTH_SHORT).show();
                levelView(arr[3]);
            }
            editor.putInt("Finish", 0); editor.putInt("Date", dateN); editor.putInt("Percent", 0);
            editor.commit();
            percentView(String.valueOf(Log.getInt("Percent", 0)), Log.getInt("Percent", 0));
            if(weekday.equals("월")) {
                if(Log.getInt("Level", 0)>=7) {
                    Toast.makeText(this, "여우와 약속 지키기 성공!", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "여우와 친구가 됐어요!", Toast.LENGTH_LONG).show();
                    for(int i = 0;  i<imageResources.length; i++) {
                        if(imageResources[i]==Log.getInt("Fox", imageResources[1])) setFoxLog(i);
                    }
                }
                Toast.makeText(this, "새로운 여우와 친구되기", Toast.LENGTH_LONG).show();
                newfox(fox);
            }

        }
        else {
            Toast.makeText(this, "오늘방문", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "마지막로그: "+arr[0], Toast.LENGTH_LONG).show();
            levelView(arr[3]);
            int percentN = Log.getInt("Percent", 0);
            String percentS = String.valueOf(percentN);
            percentView(percentS, percentN);
        }
    }

}