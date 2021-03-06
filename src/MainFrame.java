import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame implements Runnable,ActionListener,Observer {

    //private String imgFilePath = "images/map1.jpg";
    private DrawMainFrame drawMainFrame = null;
    private ParticleFilter particleFilter = null;
    private boolean isThreaAlive = true;
    static int xTrans = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width/1440;
    static int yTrans = Toolkit.getDefaultToolkit().getScreenSize().height/900;
    /*
    * 地图相关参数
    * */
    static float scale = 185;              //放大尺度
    int originX = 35*xTrans;               //原点的x
    int originY = 520*yTrans;              //原点的y
    int xAxisLength = 900*xTrans;         //X轴长度
    int yAxisLength = 510*yTrans;          //Y轴长度
    int AnchorScale = 20;           //正方形Anchor边长的一半
    static int count = 0;


    CapturedBeaconMessage capturedBeaconMessage = null;
    private volatile boolean isNewMessageCome = false;
    boolean[] isAnchorWorking = new boolean[]{false,false,false,false};
    static JTextField textx0 = new JTextField();//x0
    static JTextField texty0 = new JTextField();//y0
    static JTextField textz0 = new JTextField();//z0
    static JTextField textx1 = new JTextField();//x1
    static JTextField texty1 = new JTextField();//y1
    static JTextField textz1 = new JTextField();//z1
    static JTextField textx2 = new JTextField();//x2
    static JTextField texty2 = new JTextField();//y2
    static JTextField textz2 = new JTextField();//z2
    static JTextField textx3 = new JTextField();//x3
    static JTextField texty3 = new JTextField();//y3
    static JTextField textz3 = new JTextField();//z3
    static JTextField scaleText = new JTextField();  //scale
    static JTextField heightText = new JTextField();  //height
    static JTextField intervalText = new JTextField();  //interval
    //textx0.setText()
    static int drawCnt = 0;

    public MainFrame(ParticleFilter particleFilter) {

        //显示当前屏幕分辨率
        ScreenSize ss = new ScreenSize();
        int screenWidth = ss.getScreenWidth();
        int screenHeight = ss.getScreenHeight();
        System.out.println("屏幕宽为：" + screenWidth + "---屏幕高为：" + screenHeight);


        //ImageIcon img = new ImageIcon(imgFilePath);
        //要设置的背景图片
        JLabel imgLabel = new JLabel();
        //img.setImage(img.getImage().getScaledInstance(1000,574,Image.SCALE_DEFAULT));
        //imgLabel.setIcon(img);
        //将背景图放在图片标签里。
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        //将背景标签添加到jfram的LayeredPane面板里。

        //imgLabel.setBounds(290, 20, screenWidth, screenHeight);//图片标签(地图区域)的位置和大小


        Container contain = this.getContentPane();


        /*
        * 绘制anchor state面板上的标签
        * */
        JLabel cmdStr = new JLabel("Commands:");
        cmdStr.setBounds(1000*screenWidth/1440,5*screenHeight/900,120*screenWidth/1440,50*screenHeight/900);
        cmdStr.setFont(new   java.awt.Font("Dialog",   2,   19));
        this.getContentPane().add(cmdStr);

        JLabel stateStr = new JLabel("Anchor State");
        stateStr.setBounds(1100*screenWidth/1440,100*screenHeight/900,120*screenWidth/1440,40*screenHeight/900);
        stateStr.setFont(new   java.awt.Font("Dialog",   2,   19));
        //stateStr.setBorder(BorderFactory.createRaisedBevelBorder());
        this.getContentPane().add(stateStr);

        JLabel anchor0Str = new JLabel("Anchor 0");
        anchor0Str.setBounds(1000*screenWidth/1440,150*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor0Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor0Str);

        JLabel anchor1Str = new JLabel("Anchor 1");
        anchor1Str.setBounds(1000*screenWidth/1440,200*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor1Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor1Str);

        JLabel anchor2Str = new JLabel("Anchor 2");
        anchor2Str.setBounds(1000*screenWidth/1440,250*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor2Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor2Str);

        JLabel anchor3Str = new JLabel("Anchor 3");
        anchor3Str.setBounds(1000*screenWidth/1440,300*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        anchor3Str.setFont(new   java.awt.Font("Dialog",   2,   16));
        this.getContentPane().add(anchor3Str);

        JLabel locationStr = new JLabel("Target Location");
        locationStr.setBounds(1075*screenWidth/1440,355*screenHeight/900,150*screenWidth/1440,30*screenHeight/900);
        locationStr.setFont(new   java.awt.Font("Dialog",   2,   19));
        //locationStr.setBorder(BorderFactory.createRaisedBevelBorder());
        this.getContentPane().add(locationStr);

/*
* anchor state 区域标签绘制完毕
* */




        /*
        * 开始画参数编辑区域
        * */
        JPanel paramConfig = new JPanel();
        paramConfig.setBackground(Color.white);
        paramConfig.setLayout(new GridLayout());
        paramConfig.setBounds(150*screenWidth/1440,560*screenHeight/900,650*screenWidth/1440,262*screenHeight/900);
        paramConfig.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black) );
        this.getLayeredPane().add(paramConfig);
        paramConfig.setLayout(null);

        JLabel location = new JLabel("Location",JLabel.CENTER);
        paramConfig.add(location);
        location.setFont(new   java.awt.Font("Dialog",   2,   20));
        location.setBounds(5*screenWidth/1440,5*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        location.setBorder(BorderFactory.createRaisedBevelBorder());



        JLabel x = new JLabel("X",JLabel.CENTER);
        x.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(x);
        //x.setBorder(BorderFactory.createRaisedBevelBorder());
        x.setBounds(new Rectangle(118*screenWidth/1440, 10*screenHeight/900, 50*screenWidth/1440, 20*screenHeight/900));


        JLabel y = new JLabel("Y",JLabel.CENTER);
        y.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(y);
        //y.setBorder(BorderFactory.createRaisedBevelBorder());
        y.setBounds(new Rectangle(218*screenWidth/1440, 10*screenHeight/900, 50*screenWidth/1440, 20*screenHeight/900));

        JLabel z = new JLabel("Z",JLabel.CENTER);
        z.setFont(new   java.awt.Font("Dialog",   1,   15));
        paramConfig.add(z);
        //z.setBorder(BorderFactory.createRaisedBevelBorder());
        z.setBounds(new Rectangle(318*screenWidth/1440, 10*screenHeight/900, 50*screenWidth/1440, 20*screenHeight/900));

        JLabel anchorItem0 = new JLabel("anchor 0",JLabel.CENTER);
        paramConfig.add(anchorItem0);
        anchorItem0.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem0.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem0.setBounds(new Rectangle(10*screenWidth/1440, 51*screenHeight/900, 80*screenWidth/1440, 30*screenHeight/900));

        //JTextField textx0 = new JTextField();//x0
        paramConfig.add(textx0);
        textx0.setBounds(101*screenWidth/1440,51*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx0.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty0 = new JTextField();//y0
        paramConfig.add(texty0);
        texty0.setBounds(201*screenWidth/1440,51*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty0.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz0 = new JTextField();//z0
        paramConfig.add(textz0);
        textz0.setBounds(301*screenWidth/1440,51*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz0.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem1 = new JLabel("anchor 1",JLabel.CENTER);
        paramConfig.add(anchorItem1);
        anchorItem1.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem1.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem1.setBounds(10*screenWidth/1440,101*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);

        //JTextField textx1 = new JTextField();//x1
        paramConfig.add(textx1);
        textx1.setBounds(101*screenWidth/1440,101*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx1.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty1 = new JTextField();//y1
        paramConfig.add(texty1);
        texty1.setBounds(201*screenWidth/1440,101*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty1.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz1 = new JTextField();//z1
        paramConfig.add(textz1);
        textz1.setBounds(301*screenWidth/1440,101*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz1.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem2 = new JLabel("anchor 2",JLabel.CENTER);
        paramConfig.add(anchorItem2);
        anchorItem2.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem2.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem2.setBounds(10*screenWidth/1440,151*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);

        //JTextField textx2 = new JTextField();//x2
        paramConfig.add(textx2);
        textx2.setBounds(101*screenWidth/1440,151*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx2.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty2 = new JTextField();//y2
        paramConfig.add(texty2);
        texty2.setBounds(201*screenWidth/1440,151*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty2.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz2= new JTextField();//z2
        paramConfig.add(textz2);
        textz2.setBounds(301*screenWidth/1440,151*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz2.setHorizontalAlignment(JTextField.CENTER);

        JLabel anchorItem3 = new JLabel("anchor 3",JLabel.CENTER);
        paramConfig.add(anchorItem3);
        anchorItem3.setFont(new   java.awt.Font("Dialog",   1,   13));
        //anchorItem3.setBorder(BorderFactory.createRaisedBevelBorder());
        anchorItem3.setBounds(10*screenWidth/1440,201*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);


        //JTextField textx3 = new JTextField();//x3
        paramConfig.add(textx3);
        textx3.setBounds(101*screenWidth/1440,201*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textx3.setHorizontalAlignment(JTextField.CENTER);

        //JTextField texty3 = new JTextField();//y3
        paramConfig.add(texty3);
        texty3.setBounds(201*screenWidth/1440,201*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        texty3.setHorizontalAlignment(JTextField.CENTER);

        //JTextField textz3= new JTextField();//z3
        paramConfig.add(textz3);
        textz3.setBounds(301*screenWidth/1440,201*screenHeight/900,85*screenWidth/1440,30*screenHeight/900);
        textz3.setHorizontalAlignment(JTextField.CENTER);

        JLabel title2 = new JLabel("Other Parameters",JLabel.CENTER);
        paramConfig.add(title2);
        title2.setBorder(BorderFactory.createRaisedBevelBorder());
        title2.setFont(new   java.awt.Font("Dialog",   2,   19));
        title2.setBounds(new Rectangle(402*screenWidth/1440, 10*screenHeight/900, 200*screenWidth/1440, 30*screenHeight/900));

        JLabel textScale = new JLabel("Scale :",JLabel.CENTER);
        paramConfig.add(textScale);
        textScale.setFont(new   java.awt.Font("Dialog",   1,   13));
        textScale.setBounds(new Rectangle(402*screenWidth/1440, 51*screenHeight/900, 80*screenWidth/1440, 30*screenHeight/900));
        //textScale.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel textTargetHeight = new JLabel("Target Height :",JLabel.CENTER);
        paramConfig.add(textTargetHeight);
        textTargetHeight.setFont(new   java.awt.Font("Dialog",   1,   13));
        textTargetHeight.setBounds(new Rectangle(412*screenWidth/1440, 101*screenHeight/900, 100*screenWidth/1440, 30*screenHeight/900));
        //textTargetHeight.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel textInterval = new JLabel("Schedule Interval :",JLabel.CENTER);
        paramConfig.add(textInterval);
        textInterval.setFont(new   java.awt.Font("Dialog",   1,   13));
        textInterval.setBounds(new Rectangle(402*screenWidth/1440, 151*screenHeight/900, 130*screenWidth/1440, 30*screenHeight/900));
        textInterval.setHorizontalAlignment(JTextField.CENTER);
        //textInterval.setBorder(BorderFactory.createRaisedBevelBorder());

        //JTextField scaleText = new JTextField();  //scale
        paramConfig.add(scaleText);
        scaleText.setBounds(542*screenWidth/1440,51*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);
        scaleText.setHorizontalAlignment(JTextField.CENTER);

        //JTextField heightText = new JTextField();  //height
        paramConfig.add(heightText);
        heightText.setBounds(542*screenWidth/1440,101*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);
        heightText.setHorizontalAlignment(JTextField.CENTER);

        //JTextField intervalText = new JTextField();  //interval
        paramConfig.add(intervalText);
        intervalText.setBounds(542*screenWidth/1440,151*screenHeight/900,80*screenWidth/1440,30*screenHeight/900);
        intervalText.setHorizontalAlignment(JTextField.CENTER);



        JButton config = new JButton("Save");
        config.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ParamConfig.writeText();
                JFrame dialogFrame = new  JFrame("提示");
                dialogFrame.setBounds(600*screenWidth/1440,400*screenHeight/900,250*screenWidth/1440,150*screenHeight/900);
                dialogFrame.setResizable(false);
                dialogFrame.setLayout(null);
                dialogFrame.setVisible(true);


                JLabel tip = new JLabel("参数设置成功!");
                tip.setBounds(72*screenWidth/1440,15*screenHeight/900,110*screenWidth/1440,20*screenHeight/900);
                tip.setFont(new   java.awt.Font("Dialog",   2,   16));
                //tip.setBorder(BorderFactory.createRaisedBevelBorder());
                dialogFrame.add(tip);


                JButton ok = new JButton("OK");
                ok.setBounds(87*screenWidth/1440,60*screenHeight/900,60*screenWidth/1440,20*screenHeight/900);
                ok.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogFrame.dispose();
                        //repaint();
                    }

                });
                dialogFrame.add(ok);


            }
        });
        paramConfig.add(config);
        config.setBounds(402*screenWidth/1440,201*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        config.setFont(new   java.awt.Font("Dialog",   2,   18));
        config.setBorder(BorderFactory.createRaisedBevelBorder());
        config.addActionListener(this);



        JButton load = new JButton("Load");

        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ParamConfig.loadText();
                JFrame dialogFrame = new  JFrame("提示");
                dialogFrame.setBounds(600*screenWidth/1440,400*screenHeight/900,250*screenWidth/1440,150*screenHeight/900);
                dialogFrame.setResizable(false);
                dialogFrame.setLayout(null);
                dialogFrame.setVisible(true);


                JLabel tip = new JLabel("获取参数成功!");
                tip.setBounds(67*screenWidth/1440,15*screenHeight/900,110*screenWidth/1440,20*screenHeight/900);
                tip.setFont(new   java.awt.Font("Dialog",   2,   16));
                //tip.setBorder(BorderFactory.createRaisedBevelBorder());
                dialogFrame.add(tip);


                JButton ok = new JButton("OK");
                ok.setBounds(83*screenWidth/1440,60*screenHeight/900,60*screenWidth/1440,20*screenHeight/900);
                ok.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogFrame.dispose();
                    }
                });
                dialogFrame.add(ok);


            }
        });

        paramConfig.add(load);
        load.setBounds(522*screenWidth/1440,201*screenHeight/900,100*screenWidth/1440,30*screenHeight/900);
        load.setFont(new   java.awt.Font("Dialog",   2,   18));
        load.setBorder(BorderFactory.createRaisedBevelBorder());
/*
* 参数编辑区域绘制完毕
* */






        /*
         * 画小区域的组件
         * */
        JPanel drawCurve = new JPanel();
        drawCurve.setBackground(Color.white);
        drawCurve.setLayout(new GridLayout());
        drawCurve.setBounds(1000*screenWidth/1440,560*screenHeight/900,270*screenWidth/1440,180*screenHeight/900);
        drawCurve.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black) );
        this.getLayeredPane().add(drawCurve);
        drawCurve.setLayout(null);

        JLabel dataProcs = new JLabel("Data Process",JLabel.CENTER);
        drawCurve.add(dataProcs);
        dataProcs.setFont(new   java.awt.Font("Dialog",   2,   20));
        dataProcs.setBounds(35*xTrans,5*yTrans,200*xTrans,35*yTrans);
        //dataProcs.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton drawTrace = new JButton("Draw Trace");
        drawCurve.add(drawTrace);
        drawTrace.setFont(new   java.awt.Font("Dialog",   2,   18));
        drawTrace.setBounds(55*xTrans,55*yTrans,150*xTrans,30*yTrans);

        drawTrace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //DrawSmoothCurve.createAndShowGui();
                DrawSmoothCurve d = new DrawSmoothCurve();
                d.createAndShowGui();

            }
        });





        /*
         * 在anchor状态区设置按钮
         * */
        JButton LaunchAll = new JButton("launchAll");
        //LaunchAll.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        LaunchAll.setFont(new   java.awt.Font("Dialog",   1,   14));
        LaunchAll.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton KillAll = new JButton("killAll");
        KillAll.setFont(new   java.awt.Font("Dialog",   1,   14));
        //KillAll.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        KillAll.setBorder(BorderFactory.createRaisedBevelBorder());

        this.getLayeredPane().add(LaunchAll);
        LaunchAll.setBounds(1120*xTrans, 20*yTrans, 110*xTrans, 40*yTrans);
        this.getLayeredPane().add(KillAll);
        KillAll.setBounds(1120*xTrans, 60*yTrans, 110*xTrans, 40*yTrans);
        //加入监听器
        Listener1 L1 = new Listener1();
        LaunchAll.addActionListener(L1);
        Listener2 L2 = new Listener2();
        KillAll.addActionListener(L2);




/*
* 设置主Frame
* */

        //((JPanel) contain).setOpaque(false);
        // 将内容面板设为透明。将LayeredPane面板中的背景显示出来。

        //this.add(new MyPanel2());
        this.particleFilter = particleFilter;
        drawMainFrame = new DrawMainFrame(this.particleFilter);
        this.add(drawMainFrame);

        this.setVisible(true);
        this.setResizable(false);
        //this.setSize(1500,800);//整个窗体的大小
        this.setBounds(0, 0, screenWidth, screenHeight);
        this.setTitle("Asynchronous Localization Project: by cc at HUST");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);//指定界面默认关闭选项  EXIT_ON_CLOSE为关闭时退出程序
        //this.setLocationRelativeTo(null);// 把窗口位置设置到屏幕的中心

        //DrawSmoothCurve.createAndShowGui();


    }

    public void getCapturedBeaconMessage(CapturedBeaconMessage beaconMessage) {
        this.capturedBeaconMessage = beaconMessage;
        isNewMessageCome = true;
    }

    @Override//信号灯事件
    public void actionPerformed(ActionEvent e) {


    }

    @Override
    public void update(String msg) {

    }

    /*
     * 给按钮创建监听器
     * */
    class Listener1 implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            System.out.println("666");
            ButtonThread buttonThread1 = new ButtonThread(ButtonThread.launchAll);
            buttonThread1.start();
        }
    }

    class Listener2 implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            System.out.println("777");
            ButtonThread buttonThread2 = new ButtonThread(ButtonThread.killAll);
            buttonThread2.start();
        }
    }




    public void refresh() {

    }

    @Override
    public void run() {
        drawMainFrame.initDrawingParameters();
        while (isThreaAlive) {
            if(isNewMessageCome == true){
                isNewMessageCome = false;
                drawMainFrame.prepareAnchorState(capturedBeaconMessage);
            }
            drawMainFrame.repaint();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DrawMainFrame extends JPanel {

        ParticleFilter particleFilter = null;
        BasicStroke basicStroke = null;       // 笔画的轮廓（画笔宽度/线宽为3px）
        BasicStroke boldStroke = null;

        float[][] particles = null;
        float[][] allParticles = null;

        float maxXCoordinates = 0;

        float landmarks[][] = null;
        float targetLocation[][]=null;
        JSONUtils jsonUtils = null;
        String configFilePath = "config.txt";
        private int currentActivateAnchorId = 0;
        private boolean isMessageRecv = false;


        int numberOfParticles = ParticleFilter.topParticleNumber;
        int xInerAxis = 0;//(int) (getMaxXCoordinates() * scale);
        int yInerAxis = 0; //(int) (getMaxYCoordinates() * scale);

        Font font = null;

        /*public drawMainFrame(){ // for debug test
            this.setOpaque(false);
            //this.setBackground(Color.WHITE);
            particleFilter = new ParticleFilter(5000);
            particleFilter.generateUniformParticles(4.2f, 4.5f);
            basicStroke = new BasicStroke(5);
            particles = particleFilter.getParticles();

            copyTopParticles(numberOfParticles);
        }*/


        public void refreshParticleLocation() {
            repaint();
        }

        public float getMaxXCoordinates() {
            float maxXCoordinates = 0;
            for (int i = 0; i < landmarks.length; i++) {
                if (landmarks[i][0] > maxXCoordinates) {
                    maxXCoordinates = landmarks[i][0];
                }
            }
            return maxXCoordinates;//4.623
        }

        public float getMaxYCoordinates() {
            float maxYCoordinates = 0;
            for (int i = 0; i < landmarks.length; i++) {
                if (landmarks[i][1] > maxYCoordinates) {
                    maxYCoordinates = landmarks[i][1];
                }

            }
            return maxYCoordinates;//2.662
        }


        public DrawMainFrame(ParticleFilter particleFilter) {
            this.particleFilter = particleFilter;
            this.setOpaque(false);
            initDrawingParameters();
        }

        public void initDrawingParameters() {
            basicStroke = new BasicStroke(3);
            boldStroke = new BasicStroke(5);
            font = new Font("宋体", Font.BOLD, 15);
            //particles = particleFilter.getParticles().clone(); // deep copy
            //int index[] = particleFilter.topK(particleFilter.getWeights(), numberOfParticles);
            particles = new float[numberOfParticles][2];
            allParticles = particleFilter.getParticles();

            try {
                landmarks = jsonUtils.loadAnchorPosition(configFilePath);
               //targetLocation=jsonUtils.loadAnchorPosition("localization_2018-07-20_14_56_23.txt");

            } catch (Exception e) {

            }
            xInerAxis = (int) (getMaxXCoordinates() * scale);
            yInerAxis =  (int) (getMaxYCoordinates() * scale);

        }

        public void copyTopParticles(int n) {
            int index[] = Algorithm.topK(particleFilter.getWeights(), n);
            for (int i = 0; i < n; i++) {
                particles[i] = allParticles[index[i]];
            }
        }

/*
* 画anchor状态框、曲线显示框等需要draw的组件
*
* */
        public void paintComponent(Graphics2D g){
            //大框
            //g.drawString("123456",1200,280);
            int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
            g.setStroke(new BasicStroke(2.0f));
            g.drawRect(990*screenWidth/1440,10*screenHeight/900,310*screenWidth/1440,500*screenHeight/900);
            //小框
            g.setStroke(new BasicStroke(0.5f));
            g.drawLine(990*screenWidth/1440,105*screenHeight/900,1300*screenWidth/1440,105*screenHeight/900);
            g.drawLine(990*screenWidth/1440,355*screenHeight/900,1300*screenWidth/1440,355*screenHeight/900);



        }

        /*
         * 所有动态东西都在这里面画
         * */
        public void paint(Graphics g) {

            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setStroke(basicStroke);
            g2.setColor(Color.WHITE);
            // first erase the last particles 擦除旧点
            if (particles != null) {
                for (int i = 0; i < this.particles.length; i++) {
                    drawPoints(particles[i][0], particles[i][1], g2);
                }
            }

            copyTopParticles(numberOfParticles);
            // second draw the new particles  画新点
            g2.setColor(Color.RED);
            for (int i = 0; i < particles.length; i++) {
                drawPoints(particles[i][0], particles[i][1], g2);
            }

            // draw the estimated locations
            g2.setColor(Color.GREEN);
            g2.setStroke(boldStroke);
            //drawEstimatedLocation(, 1, g2);画估计点
            drawEstimatedLocation(particleFilter.getX(), particleFilter.getY(), g2);
            //画坐标轴
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2.0f));
            drawAxis(originX, originY, g2);

            //用虚线画上下边界
            g2.setColor(Color.CYAN);
            //g2.setStroke(new BasicStroke(2.0f));
            Stroke dash = new BasicStroke(2.5f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 3.5f, new float[]{5, 5,},
                    0f);
            g2.setStroke(dash);
            drawBorder(originX, originY, g2);

            // add labels for the estimated locations
            g2.setColor(Color.darkGray);
            //drawLabels(particleFilter.getX(), particleFilter.getY(), String.format( " (%.2f ,  %.2f ,  %.2f)", particleFilter.getX(), particleFilter.getY(), particleFilter.getZ()), g2);
            g2.setFont(new   java.awt.Font("Dialog",   2,   18));
            g2.drawString(String.format( " x = %.2f ", particleFilter.getX()),1100*xTrans, 420*yTrans);
            g2.drawString(String.format( " y = %.2f ", particleFilter.getY()),1100*xTrans
                    , 450*yTrans);
            g2.drawString(String.format( " z = %.2f ", particleFilter.getZ()),1100*xTrans, 480*yTrans   );

            //画Anchor
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            drawAnchor(AnchorScale, g2,1);
            paintComponent(g2);
            initAnchorState(g2);
            if(isMessageRecv) {
                //标记当前发出消息的anchor
               freshAnchorState(g2, currentActivateAnchorId);
                //画出各个anchor的工作状态
                drawAnchorWorkingState(g2);
            }
            repaint();
        }

        public void prepareAnchorState(CapturedBeaconMessage capturedBeaconMessage){
            isAnchorWorking[capturedBeaconMessage.selfAnchorId] = true;
            currentActivateAnchorId = capturedBeaconMessage.capturedAnchorId;
            isMessageRecv = true;
        }

        //画点
        public void drawPoints(float x, float y, Graphics2D g) {
            g.drawLine((int) (x * scale*xTrans + originX), (int) (originY - y * scale*yTrans), (int) (x * scale*xTrans + originX), (int) (originY - y * scale*yTrans));
        }
        /*
        public void drawEstimatedLocation(float x, float y, Graphics2D g){
            g.drawLine((int)(x * scale) - 5,(int)(y * scale), (int)(x * scale)+5,(int)(y * scale));
            g.drawLine((int)(x * scale), (int)(y * scale)+5, (int)(x * scale), (int)(y * scale)-5);
        }
        */

        //画估计位置
        public void drawEstimatedLocation(float x, float y, Graphics2D g) {
            g.drawLine((int) ((x*scale + originX))*xTrans - 5, (int) (originY - y*scale)*yTrans, (int) ((x*scale + originX) + 5)*xTrans, (int) (originY - y*scale)*yTrans);
            g.drawLine((int) (x*scale + originX)*xTrans, (int) ((originY - y*scale) + 5)*yTrans, (int) (x*scale + originX)*xTrans, (int) ((originY - y*scale) - 5)*yTrans);
        }

        public void drawAxis(float x, float y, Graphics2D g) {
            drawAxisX(x, y, g);
            drawAxisY(x, y, g);
        }

        //画x轴
        public  void drawAxisX(float x, float y, Graphics2D g) {
            g.drawLine((int) x, (int) y, (int) x + xAxisLength, (int) y);
            g.drawLine((int) x + xAxisLength, (int) y, (int) x + xAxisLength - 5, (int) y - 2);
            g.drawLine((int) x + xAxisLength, (int) y, (int) x + xAxisLength - 5, (int) y + 2);
            g.setFont(new Font("宋体", Font.BOLD, 20));
            g.drawString("X", (int) x + xAxisLength + 5, (int) y);

        }

        //画y轴
        public void drawAxisY(float x, float y, Graphics2D g) {
            g.drawLine((int) x, (int) y, (int) x, (int) y - yAxisLength);
            g.drawLine((int) x, (int) y - yAxisLength, (int) x - 2, (int) y - yAxisLength + 5);
            g.drawLine((int) x, (int) y - yAxisLength, (int) x + 2, (int) y - yAxisLength + 5);
            g.setFont(new Font("宋体", Font.BOLD, 20));
            g.drawString("Y", (int) x - 15, (int) y - yAxisLength + 5);
        }

        //画上、右边界
        public void drawBorder(float x, float y, Graphics2D g) {
            int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

            g.drawLine((int) (x + 2), (int) (y - yInerAxis*screenHeight/900), (int) (x + xInerAxis*screenWidth/1440), (int) (y - yInerAxis*screenHeight/900));//上边界
            g.drawLine((int) (x + xInerAxis*screenWidth/1440), (int) y, (int) (x + xInerAxis*screenWidth/1440), (int) (y - yInerAxis*screenHeight/900));//右边界
            g.setFont(new Font("宋体", Font.BOLD, 15));
            g.setColor(Color.black);
            g.drawString(String.valueOf(getMaxXCoordinates()) + "m", (int) (x - 13 + xInerAxis*screenWidth/1440), (originY + 16));
            g.drawString(String.valueOf(getMaxYCoordinates()) + "m", originX + 3, (int) y - yInerAxis*screenHeight/900 - 5);
        }


        /*
        //画坐标标签
        public void drawLabels(float x, float y, String msg, Graphics2D g) {
            //g.setFont(font);
            g.setFont(new   java.awt.Font("Dialog",   1,   18));
            g.drawString(msg, 1000, 500);

        }
        */

        //画Anchor
        public void drawAnchor(int r, Graphics2D g,int type) {
            String ss[] = new String[]{"0", "1", "2", "3"};
            if(type==1) {
                for (int i = 0; i < 4; i++) {
                    g.drawRect((int) (landmarks[i][0] * scale * xTrans) - r / 2 * xTrans + originX, (originY - (int) (landmarks[i][1] * scale * yTrans) - r / 2 * yTrans), r * xTrans, r * yTrans);
                    g.drawString(ss[i], ((landmarks[i][0] * scale * xTrans + originX) - (r) / 4), ((originY - landmarks[i][1] * scale * yTrans) + (r - 4 - r / 2)));
                }
            }
            else if(type==2){
                for (int i = 0; i < 4; i++) {
                    g.drawRect((int) (((landmarks[i][0] * scale * xTrans) - r / 2 * xTrans + originX)*1.5), (int) (((originY - (int) (landmarks[i][1] * scale * yTrans) - r / 2 * yTrans))*1.5), (int) (r * xTrans*1.5), (int) (r * yTrans*1.5));
                    g.drawString(ss[i], (int) (((landmarks[i][0] * scale * xTrans + originX) - (r) / 4+2)*1.5), (int) (((originY - landmarks[i][1] * scale * yTrans) + (r - 4 - r / 2)-2)*1.5));
                }
            }

        }



        //画当前发送消息的anchor的指示灯
        public void freshAnchorState(Graphics2D g,int AnchorNum) {
            int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
            g.setFont(new Font("MS 明朝", Font.BOLD, 20));
            g.setStroke(new BasicStroke(4f));

            //清除上一次的指示灯
            g.setColor(Color.WHITE);
            for(int i = 0;i<4;i++)
            {
                g.drawRect((int) ((landmarks[i][0] * scale*xTrans + originX - AnchorScale / 2*xTrans) - 3*xTrans), (int) ((originY - landmarks[i][1] * scale*yTrans - AnchorScale / 2*yTrans) - 3*yTrans), (AnchorScale + 7)*xTrans, (AnchorScale + 7)*yTrans);
            }


            //画出正在发送信息的Anchor指示灯(绿)
            g.setColor(Color.GREEN);
            switch(AnchorNum){
                case 0:
                {g.drawRect((int) ((landmarks[0][0] * scale*screenWidth/1440 + originX - AnchorScale / 2*screenWidth/1440) - 3*screenWidth/1440), (int) ((originY - landmarks[0][1] * scale*screenHeight/900 - AnchorScale / 2*screenHeight/900) - 3*screenHeight/900), (AnchorScale + 7)*screenWidth/1440, (AnchorScale + 7)*screenHeight/900);
                    isAnchorWorking[0]=true;
                    break;}
                case 1:
                {g.drawRect((int) ((landmarks[1][0] * scale*screenWidth/1440 + originX - AnchorScale / 2*screenWidth/1440) - 3*screenWidth/1440), (int) ((originY - landmarks[1][1] * scale*screenHeight/900 - AnchorScale / 2*screenHeight/900) - 3*screenHeight/900), (AnchorScale + 7)*screenWidth/1440, (AnchorScale + 7)*screenHeight/900);
                    isAnchorWorking[1]=true;
                    break;}
                case 2:
                {g.drawRect((int) ((landmarks[2][0] * scale*screenWidth/1440 + originX - AnchorScale / 2*screenWidth/1440) - 3*screenWidth/1440), (int) ((originY - landmarks[2][1] * scale*screenHeight/900 - AnchorScale / 2*screenHeight/900) - 3*screenHeight/900), (AnchorScale + 7)*screenWidth/1440, (AnchorScale + 7)*screenHeight/900);
                    isAnchorWorking[2]=true;
                    break;}
                case 3:
                {g.drawRect((int) ((landmarks[3][0] * scale*screenWidth/1440 + originX - AnchorScale / 2*screenWidth/1440) - 3*screenWidth/1440), (int) ((originY - landmarks[3][1] * scale*screenHeight/900 - AnchorScale / 2*screenHeight/900) - 3*screenHeight/900), (AnchorScale + 7)*screenWidth/1440, (AnchorScale + 7)*screenHeight/900);
                    isAnchorWorking[3]=true;
                    break;}
            }




        }


        public void initAnchorState(Graphics2D g){
            //g.setFont(new Font("宋体", Font.BOLD, 20));
            int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

            g.setStroke(new BasicStroke(1f));
            g.setFont(new   java.awt.Font("Dialog",   3,   15));
            g.setColor(Color.BLACK);
            //g.drawString("Anchor state",1150,140);
//            g.drawString("Anchor_0:",1000,180);
//            g.drawString("Anchor_1:",1000,230);
//            g.drawString("Anchor_2:",1000,280);
//            g.drawString("Anchor_3:",1000,330);



            g.setFont(new Font("宋体", Font.BOLD, 40));
            g.setColor(Color.red);
            for(int i =0;i<4;i++){
                g.drawString("■",1130*xTrans,(185+i*50)*yTrans);
            }

        }


        public void drawAnchorWorkingState(Graphics2D g)
        {

            int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
            for(int i =0;i<4;i++){
                g.setFont(new Font("宋体", Font.BOLD, 40));
                if(isAnchorWorking[i]==true){
                    g.setColor(Color.green);
                    g.drawString("●",1130*xTrans,(185+i*50)*yTrans);
                }
                else{
                    g.setColor(Color.red);
                    g.drawString("■",1130*xTrans,(185+i*50)*yTrans);
                }
            }
        }
    }


    /*
     *内部类DrawSmoothCurve
     * 该类用来绘制新窗口的曲线
     * */
    class DrawSmoothCurve extends JPanel {
         int[][] array;

        {
            try {
                array = new getLocation().getData();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public  Point[] getPoints(){
            Point[] data = new Point[array[0].length];
            for(int i=0;i<array[0].length;i++){
                data[i] = new Point(array[0][i],array[1][i]);
            }
            return data;
        }



//        private Point[] points = {
//                new Point(5,5),
//                new Point(9,0),
//                new Point(100, 300),
//                new Point(100, 100),
//                new Point(200, 100),
//                new Point(300, 100),
//                new Point(330, 80),
//                new Point(150, 70),
//                new Point(0,0)
//        };
       private Point[] points = getPoints();



        GeneralPath path = new GeneralPath();

        public DrawSmoothCurve() {
            path.moveTo(points[0].x, points[0].y);

            for (int i = 0; i < points.length-1; ++i) {
                Point sp = points[i];
                Point ep = points[i+1];
                Point c1 = new Point((sp.x + ep.x)/2, sp.y);
                Point c2 = new Point((sp.x + ep.x)/2, ep.y);

                path.curveTo(c1.x, c1.y, c2.x, c2.y, ep.x, ep.y);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect((int) (originX*xTrans*1.5),(int) ((originY-drawMainFrame.yInerAxis)*1.5*yTrans),(int)(drawMainFrame.xInerAxis*xTrans*1.5),(int)(drawMainFrame.yInerAxis*yTrans*1.5));
            g2d.translate(0, 0);//原点
            g2d.setStroke(new BasicStroke(1.5f));
            drawMainFrame.drawAnchor(AnchorScale,g2d,2);
            g2d.draw(path);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.0f));

            for (int i = 0; i < points.length; ++i) {
                g2d.setColor(Color.GRAY);
                g2d.fillOval(points[i].x-4, points[i].y-4, 8, 8);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(points[i].x-4, points[i].y-4, 8, 8);
            }
        }



        public void createAndShowGui() {
            JFrame frame = new JFrame("Target Trace");
            frame.setContentPane(new DrawSmoothCurve());
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400*xTrans, 850*yTrans);
            frame.setResizable(false);
            frame.setLocation(10,10);
            frame.setLayout(null);
            //frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            JButton savePic = new JButton("Trace");
            frame.add(savePic);
            savePic.setBounds(800*xTrans,600*yTrans,100*xTrans,30*yTrans);

            savePic.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    savePic(frame);
                }
            });

        }


        public  void  savePic(JFrame jf){
            //得到窗口内容面板
            Container content=jf.getContentPane();
            //创建缓冲图片对象
            BufferedImage img=new BufferedImage(
                    jf.getWidth(),jf.getHeight(),BufferedImage.TYPE_INT_RGB);
            //得到图形对象
            Graphics2D g2d = img.createGraphics();
            //将窗口内容面板输出到图形对象中
            content.printAll(g2d);
            //保存为图片
            SimpleDateFormat df = new SimpleDateFormat("MM-dd_HH_mm");//设置日期格式
            File f=new File("trace/Trace"+df.format(new Date())+".jpg");
            try {
                ImageIO.write(img, "jpg", f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //释放图形对象
            g2d.dispose();
        }
    }


    class Data{
        private double x;
        private double y;
        Data(double x,double y){
            this.x=x;
            this.y=y;
        }
        public double getX() {
            return x;
        }
        public void setX(double x) {
            this.x = x;
        }
        public double getY() {
            return y;
        }
        public void setY(double y) {
            this.y = y;
        }
    }
    class getLocation {
        public  int[][] getData() throws IOException {
            String path = "C:\\Users\\cc\\IdeaProjects\\server\\results";
            String latestPath = FileUtils.getLatestFile(path);//找到最新生成的文本文档
            System.out.println(latestPath);

            BufferedReader br=new BufferedReader(new FileReader("results/"+latestPath));
            List<String> list=new ArrayList<>();

            String str1 = null;
            //String str2=null;
            while((str1=br.readLine())!=null) {
                String[] str=str1.split("	");
                list.add(str[0]);
                list.add(str[1]);
            }
            int length=list.size();
            String[] array=list.toArray(new String[0]);
            List<Data> plist=new ArrayList<>();
            for(int i=0;i<length;) {
                plist.add(new Data(Double.parseDouble(array[i++]),Double.parseDouble(array[i++])));
            }
            Data[] data=plist.toArray(new Data[0]);
//        for(int i=0;i<(length/2);i++)
//            System.out.println(data[i].getX()+","+data[i].getY());
//        br.close();
            int[][] arr = new int[2][data.length];
            for(int i=0;i<data.length;i++){
                arr[0][i] = (int) ((data[i].getX()*scale*xTrans+originX)*1.5);
                arr[1][i] = (int)((originY-data[i].getY()*scale*xTrans)*1.5);
            }

            return arr;
        }
    }



}
//■●







//以下代码用于显示当前屏幕分辨率
class ScreenSize {
    private int screenWidth;
    private int screenHeight;

    public int getScreenWidth() {

        setScreenWidth(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        setScreenHeight(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}









