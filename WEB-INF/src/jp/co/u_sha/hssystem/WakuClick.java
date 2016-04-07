package jp.co.u_sha.hssystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class WakuClick extends HttpServlet{

	private static final long serialVersionUID = -785676005307619204L;
	
	static final int RSV_AKI = 0;
	static final int RSV_KARI = 1;
	static final int RSV_KAKUTEI = 2;
	static final long TIMEOUT_KARI = 1000;//600000;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn;
		PreparedStatement psSel,psUp;
		response.setContentType("text/html; charset=Shift_JIS");
		PrintWriter out = response.getWriter();
		
	    String url = "jdbc:mysql://localhost/hss";
	    String user = "testuser";
	    String pass = "testpass";
	    try{
	    	Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	conn = DriverManager.getConnection(url, user, pass);
	    	psSel = conn.prepareStatement("SELECT * FROM rsvlist WHERE rsvDate=? AND rsvTime=? AND Biyoshi=?");
	    	psUp = conn.prepareStatement("UPDATE rsvlist SET Status=?, CustId=?, Messages=?, Menu=?, LastUpdate=? WHERE rsvDate=? AND rsvTime=? AND Biyoshi=?");
	    	
	    	HttpSession session = request.getSession(false);
	    	if(session == null){
	    		//ログイン未処理のエラーのHTMLを出力して終了
	    		printFailDocs(out,"セッションが開始されていません。");
	    	}
	    	String loginUserID = (String)session.getAttribute("userID");
	    	//選択された予約枠の情報を取得する
	    	String rsvDate = request.getParameter("rsvDate"); // 予約枠（予約年月日)
	    	String rsvTime = request.getParameter("rsvTime"); // 予約枠（予約時刻）
	    	String rsvBiyoshi = request.getParameter("Biyoshi"); //予約枠（担当美容師）
	    	
	    	//入力された４つの変数をチェック
	    	//入力値チェックで問題があったらエラーHTMLをｐ出力して終了する。
	    	
	    	//選択された予約枠の予約状況を参照する。
	    	psSel.setString(1, rsvDate); psSel.setString(2, rsvTime); psSel.setString(3, rsvBiyoshi);
	    	ResultSet rs = psSel.executeQuery();
	    	rs.next();
	    	long lastDateTime = rs.getLong("LastUpdate");
	    	//現在の時刻を取得する
	    	long nowDateTime  = new Date().getTime();
	    	//すでに仮予約であった場合、仮予約の最終更新時刻を確認し、TIMEOUT_KARI経過後なら空きにする
	    	int rsvStatus = rs.getInt("Status");
	    	if(rsvStatus == RSV_KARI){
	    		if((nowDateTime - lastDateTime) > TIMEOUT_KARI){
	    			rsvStatus = RSV_AKI;
	    		}
	    	}
	    	//予約状況が空きであることを確認し、仮予約処理を行う。
	    	if(rsvStatus == RSV_AKI){
	    		psUp.setInt(1, RSV_KARI); psUp.setString(2, loginUserID); psUp.setString(3, "");
	    		psUp.setString(4, ""); psUp.setLong(5, nowDateTime);
	    		psUp.setString(6, rsvDate);
	    		psUp.setString(7, rsvTime); psUp.setString(8, rsvBiyoshi);
	    		psUp.executeUpdate();
	    		//図６の画面処理に引き継ぐために、選択した予約枠の情報をサーブレットのセッション情報に保存する
	    		//画面３のHTML出力を行う
	    		printYoyakuDocs(out, rsvDate, rsvTime, rsvBiyoshi);
	    	}else{
	    		//予約失敗画面のHTML出力を行う
	    		printFailDocs(out,"予約に空きがありません。");
	    	}
	    	//省略　後処理を行う
	    	
	    }catch(Exception e){
	    	//適切なエラー処理。
	    	printFailDocs(out,e.getMessage());
	    }
	   //その他必要なメソッド

	}
	private void printYoyakuDocs(PrintWriter out,String date,String time, String biyoshi){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>画面3　本予約</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<p><h1>予約日時");
		out.println(date + "[" + time + "]担当美容師:" + biyoshi);
		out.println("</h1></p>");
		out.println("<form action=\"kakuteiclick\" method=\"POST\">");
		out.println("<p><label>注文メニュー：<select name=\"Menu\">");
		out.println("<option value=\"VIP\">VIPコース</option>");
		out.println("<option value=\"STD\">標準コース</option>");
		out.println("</select></label></p>");
		out.println("<p><label>美容師へのメッセージ<textarea name=\"Message\" cols=\"50\" rows=\"10\"></textarea></label></p>");
		out.println("<input type=\"hidden\" name=\"rsvDate\" value=\"" + date +"\">");
		out.println("<input type=\"hidden\" name=\"rsvTime\" value=\"" + time +"\">");
		out.println("<input type=\"hidden\" name=\"Biyoshi\" value=\"" + biyoshi +"\">");
		out.println("<input type=\"submit\" value=\"予約する\"/>");
		out.println("</form>");
		out.println("</body>");
		out.println("</html>");
	}
	
	private void printFailDocs(PrintWriter out,String msg){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>画面3　本予約</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("予約に失敗しました。" + msg);
		out.println("</body>");
		out.println("</html>");
	}
}
