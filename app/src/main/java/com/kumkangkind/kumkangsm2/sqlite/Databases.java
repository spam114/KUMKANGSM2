package com.kumkangkind.kumkangsm2.sqlite;

import android.provider.BaseColumns;

public final class Databases {
	
	
	/**
	 *  (mailbox) 테이블 생성
	 * @author hoosik
	 *
	 */
	public static final class CreateDB implements BaseColumns{

		public static final String _TABLENAME = "mailbox";

		public static final String HEADER = "header";
		public static final String CONTENTS = "contents";
		public static final String TIME = "time";


		public static final String _CREATE = 
			"create table "+_TABLENAME+"(" 
					+_ID+" integer primary key autoincrement, " 	
					+HEADER+" text not null , " 
					+CONTENTS+" text not null , " 
					+TIME+" text not null );";
	}
}