package day05;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 面向过程版本
 */
public class Yang {
    public static void main(String[] args) {
        JFrame frame = new JFrame("羊了个羊");
        JPanel panel = new JPanel();
        JLabel background = new JLabel(new ImageIcon("images/背景.jpg"));
        background.setSize(480,800);
        background.setLocation(0,0);
        panel.setLayout(null);
        panel.add(background);

        ArrayList<JButton> though = new ArrayList<>();

        //1. 创建一副牌
        ArrayList<JButton> cards = createCards(6);
        print(cards);
        //2. 洗牌
        Collections.shuffle(cards);
        print(cards);

        //3. 摆放
        deal(panel, cards, 0,6,7,30, 100);
        deal(panel, cards, 42, 5, 6, 60,133);
        deal(panel, cards, 42+30, 5, 7, 30,166);
        //4. 检查是否在最上层
        checkCards(cards);
        //5. 为每个牌增加动作事件
        addAction(cards, though);

        frame.add(panel);
        frame.setSize(495, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * 为每个牌增加点击事件
     * @param cards 全部的牌
     * @param though 槽子
     */
    public static void addAction(ArrayList<JButton> cards, ArrayList<JButton> though){
        for (int i=0 ;i<cards.size(); i++){
            JButton card = cards.get(i);
            card.addActionListener(e -> {
                JButton selected = (JButton) e.getSource();
                System.out.println("点击："+selected.getName());
                if (though.size() == 7){
                    JOptionPane.showMessageDialog(selected, "槽子满了！");
                    return;
                }
//                if (though.contains(selected)){
//                    return;
//                }
                cards.remove(selected);
                //去除按钮上的动作事件
                ActionListener l = selected.getActionListeners()[0];
                selected.removeActionListener(l);
                selected.setLocation(20+though.size()*63, 642);
                //though.add(selected);
                checkCards(cards);
                updateThough(though, selected);
            });
        }
    }
    /**
     * 更新槽子中牌的显示
     */
    public static void updateThough(ArrayList<JButton> though,
                                    JButton selected){
        //1. 对槽子中的牌进行排序
        //though.sort((c1,c2)->c1.getName().compareTo(c2.getName()));
        int found = -1;
        for (int i=0; i<though.size(); i++){
            JButton card = though.get(i);
            if (card.getName().equals(selected.getName())){
                though.add(i, selected);
                found = i;
                break;
            }
        }
        if (found == -1){
            though.add(selected);
        }
        //3 消除连续的3张牌
        if (found != -1){
            //如果 found 不是 -1，也就是中间插入，时候开始消除
            //当前位置开始，到最后至少有3张牌
            if (though.size()-found >= 3){
                JButton nextNextCard = though.get(found+2);
                if (selected.getName().equals(nextNextCard.getName())){
                    //Parent 父，是当前按钮所在的面板
                    JPanel panel = (JPanel)selected.getParent();
                    panel.remove(though.remove(found));
                    panel.remove(though.remove(found));
                    panel.remove(though.remove(found));
                    //重新绘制panel，解决删除的残影
                    panel.repaint();
                }
            }
        }
        //2. 根据排序的结果， 重新设置 每个牌的 Location
        for (int i=0; i<though.size(); i++){
            JButton card = though.get(i);
            int x = 25 + 62 * i;
            int y = 642;
            card.setLocation(x, y);
        }
    }


    /**
     * 检查每个牌，是否被压住，压住的牌设置为 disable，在上层的牌设置为 enable
     * check : 检查
     */
    public static void checkCards(ArrayList<JButton> cards){
        for(int i=0; i<cards.size(); i++){
            JButton card1 = cards.get(i);
            card1.setEnabled(true);
            for (int j=i+1; j<cards.size(); j++){
                JButton card2 = cards.get(j);
                int x1 = card1.getX() - 59;
                int x2 = card1.getX() + 59;
                int y1 = card1.getY() - 66;
                int y2 = card1.getY() + 66;
                if ((card2.getX() > x1 && card2.getX() < x2) &&
                        (card2.getY() > y1 && card2.getY() < y2)){
                    card1.setEnabled(false);
                }
            }
        }
    }

    /**
     * row 行
     * column 列
     * 摆放牌
     * panel 摆放牌的面板
     * cards 牌的集合
     * start 开始，是只从那个cards位置开始摆放     0 42
     * rows 行数
     * cols 列数
     * x, y 是第一张牌左上角的位置
     *     deal(panel, cards, 0, 6, 7, 30, 100)
     */
    public static void deal(JPanel panel, ArrayList<JButton> cards,
                            int start, int rows, int cols, int x, int y){
        for(int i=0; i<rows * cols ; i++){
            //  96     ==  96 到达cards
            if (i+start == cards.size()){
                //到达cards末尾就结束
                return;
            }
            int x1 = x + i % cols * 59;
            int y1 = y + i / cols * 66;
            JButton card = cards.get(i + start);
            card.setLocation(x1, y1);
            card.setEnabled(false);
            panel.add(card, 0);
        }
    }

    /**
     * 打印全部的牌
     * @param cards 牌的集合
     */
    public static void print(ArrayList<JButton> cards){
        for(int i=0; i<cards.size(); i++){
            JButton card = cards.get(i);
            System.out.print(card.getName()+" ");
            if ((i+1) % 7 == 0){
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * create: 创建就 createCards 创建牌
     * 定义一个方法， 封装牌的创建过程，返回值是创建好的全部牌
     * round 参数代表轮次，本案例中需要使用 3的倍数
     * @return 一堆牌（按钮）
     */
    public static ArrayList<JButton> createCards(int round){
        String[] names = {"刷子","剪刀","叉子","奶瓶","干草","手套","树桩",
                "棉花","毛线","水桶","火","玉米","白菜","草","萝卜","铃铛"};
        ArrayList<JButton> cards = new ArrayList<>();
        for (int i=0; i<round; i++){
            for(int j=0; j<names.length; j++){
                String filename = "images/"+ names[j] + ".png";
                String filename2 = "images/"+ names[j] + "2.png";
                JButton card = new JButton(new ImageIcon(filename));
                card.setDisabledIcon(new ImageIcon(filename2));
                card.setSize(59, 66);
                card.setName(names[j]);
                cards.add(card);
            }
        }
        return cards;
    }
}
