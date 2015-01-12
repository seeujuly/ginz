package com.ginz.util.base;

/**
 * 通用字典工具类
 */
public class DictionaryUtil {

	//账户类型
	public static final String ACCOUNT_TYPE_01 = "1";	//个人用户
	public static final String ACCOUNT_TYPE_02 = "2";	//社区用户(物业)
	public static final String ACCOUNT_TYPE_03 = "3";	//商户
	
	//账户状态
	public static final String ACCOUNT_STATUS_00 = "0";	//正常
	public static final String ACCOUNT_STATUS_01 = "1";	//异常
	
	//删除标识
	public static final String DETELE_FLAG_00 = "0";	//正常
	public static final String DETELE_FLAG_01 = "1";	//删除
	
	//是否标识
	public static final String STATE_YES = "0";	//是
	public static final String STATE_NO = "1";	//否

	//情感状态
	public static final String EMOTIONAL_STATE_00 = "0";	//单身
	public static final String EMOTIONAL_STATE_01 = "1";	//交往中
	public static final String EMOTIONAL_STATE_02 = "2";	//已婚
	public static final String EMOTIONAL_STATE_03 = "3";	//已育
	
	//发布信息类型
	public static final String RELEASE_TYPE_01 = "1";	//社区公告
	public static final String RELEASE_TYPE_02 = "2";	//社区生活
	public static final String RELEASE_TYPE_03 = "3";	//活动/交易
	public static final String RELEASE_TYPE_04 = "4";	//社区商户
	public static final String RELEASE_TYPE_05 = "5";	//互联网商户
	
	//发布信息状态
	public static final String RELEASE_MSG_STATE_00 = "0";	//开放
	public static final String RELEASE_MSG_STATE_01 = "1";	//关闭
	
	//图片目录
	public static final String PIC_HEAD_PORTRAIT = "/head/";	//头像照
	public static final String PIC_BACKGROUND = "/bg/";	//背景图片
	public static final String PIC_RELEASE_NOTICE = "/notice/";	//社区公告
	public static final String PIC_RELEASE_INTERACTIVE = "/interactive/";	//积分互动
	public static final String PIC_RELEASE_EVENT = "/event/";	//个人动态
	public static final String PIC_RELEASE_ACTIVITY = "/activity/";	//社区生活(活动)
	public static final String PIC_THUMBNAIL = "/thumbnail";	//缩略图目录
	
	//图片类型标识
	public static final String PIC_TYPE_PORTRAIT = "1";	//头像照
	public static final String PIC_TYPE_BG = "2";	//背景图片
	public static final String PIC_TYPE_RELEASE = "3";	//信息插图
	
}
