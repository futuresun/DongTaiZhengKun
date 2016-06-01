package com.dongtaizhengkun.utils;


import android.content.Context;

/**
 * Created by Administrator on 2016/5/31.
 */
public class DBAdapter {
    //发货联系人
    public static final String SEND_CONTACT_SQL = "create table send_contact(" +
                                                    "name varchar(20)," +
                                                    "num int," +
                                                    "id int)";
    //收货联系人
    public static final String RECV_CONTACT_SQL = "create table recv_contact(" +
                                                    "name varchar(20)," +
                                                    "num int)";
    //今日订单详情
    public static final String TODAY_ORDERS_SQL = "create table today_orders(" +
                                                    "netpoint int," +
                                                    "netname varchar(20)," +
                                                    "senddate varchar(30)," +
                                                    "goodno varchar(30)," +
                                                    "sendname varchar(20)," +
                                                    "sendnum int," +
                                                    "sendid int," +
                                                    "recvname varchar(20)," +
                                                    "recvnum int," +
                                                    "out varchar(20)," +
                                                    "pinname varchar(20)," +
                                                    "count int," +
                                                    "package varchar(20)," +
                                                    "baojia int," +
                                                    "xianfu int," +
                                                    "tifu int," +
                                                    "daishou int," +
                                                    "shouli varchar(20)," +
                                                    "beizhu varchar(255))";

    public static DBHelper send_contact;
    public static DBHelper recv_contact;
    public static DBHelper today_orders;

    public DBAdapter(Context context) {
        send_contact = (send_contact==null? new DBHelper(context, "send_contact", null, 1, SEND_CONTACT_SQL) : send_contact);
        recv_contact = (recv_contact==null? new DBHelper(context, "recv_contact", null, 1, RECV_CONTACT_SQL) : recv_contact);
        today_orders = (today_orders==null? new DBHelper(context, "today_orders", null, 1, TODAY_ORDERS_SQL) : today_orders);
    }
}
