package com.aw.theArtOfJavaConcurrencyProgramming;

public class Chapter11 {
    public static void main(String[] args) {
        System.out.println("��11�¡�Java�������ʵ��");
    }
}
/**
 ��������:
 top => 1 �鿴ÿ��CPU����������
 top => H �鿴ÿ���̵߳�������Ϣ
 �����߳�dump
 sudo -u admin jstack �߳�Pid > /1.dump
 dump�е��߳�ID��ʮ�����Ƶ�
 printf "%x\n" 31558 ʮ����ת16����
 cat /proc/net/dev �鿴��������
 cat /proc/loadavg �鿴ϵͳƽ������
 cat /proc/meminfo �鿴ϵͳ�ڴ����
 cat /proc/stat �鿴cpu��������
 */
