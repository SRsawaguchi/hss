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

public class KakuteiClick extends HttpServlet{

	private static final long serialVersionUID = -6143844561068413157L;
	
	static final int RSV_AKI = 0;
	static final int RSV_KARI = 1;
	static final int RSV_KAKUTEI = 2;

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
	    	psSel = conn.prepareStatement("SELECT * FROM rsvlist INNER JOIN custmaster ON rsvlist.CustID=custmaster.CustID WHERE rsvDate=? AND rsvTime=? AND Biyoshi=?");
	    	psUp = conn.prepareStatement("UPDATE rsvlist SET Status=?, CustID=?, Messages=?, Menu=?, LastUpdate=? WHERE rsvDate=? AND rsvTime=? AND Biyoshi=?");
	    	
	    	HttpSession session = request.getSession(false);
	    	if(session == null){
	    		//ログイン未処理のエラーのHTMLを出力して終了
	    		printFailDocs(out,"セッションが開始されていません。");
	    	}
	    	
	    	//選択された予約枠の情報を取得する
	    	String rsvDate = request.getParameter("rsvDate"); // 予約枠（予約年月日)
	    	String rsvTime = request.getParameter("rsvTime"); // 予約枠（予約時刻）
	    	String rsvBiyoshi = request.getParameter("Biyoshi"); //予約枠（担当美容師）
	    	String loginUserID = (String)session.getAttribute("userID"); //ログイン時の顧客ID
	    	//本予約画面で入力された情報を取得する
	    	String serviceMenu = request.getParameter("Menu");
	    	String toShop = request.getParameter("Message");
	    	//入力された６つの変数をチェック
	    	//入力値チェックで問題があったらエラーHTMLをｐ出力して終了する。
	    	//指定日時の予約状況を参照する
	    	psSel.setString(1, rsvDate); psSel.setString(2, rsvTime);
	    	psSel.setString(3, rsvBiyoshi);
	    	ResultSet rs = psSel.executeQuery();
	    	rs.next();
	    	int rsvStatus = rs.getInt("Status");
	    	String rsvUserID = rs.getString("CustID");
	    	//現在の時刻を取得する
	    	long nowDateTime = new Date().getTime();
	    	//予約状況が仮であり、自分の予約であることを確認する
	    	if(rsvStatus == RSV_KARI && rsvUserID.equals(loginUserID)){
	    		//予約状況を確定に変更する
	    		psUp.setInt(1, RSV_KAKUTEI); psUp.setString(2, loginUserID);
	    		psUp.setString(3, toShop); psUp.setString(4, serviceMenu);
	    		psUp.setLong(5, nowDateTime); psUp.setString(6, rsvDate);
	    		psUp.setString(7, rsvTime); psUp.setString(8, rsvBiyoshi);
	    		psUp.executeUpdate();
	    		psSel.setString(1, rsvDate); psSel.setString(2, rsvTime);
		    	psSel.setString(3, rsvBiyoshi);
		    	rs = psSel.executeQuery();
		    	rs.next();
		    	//rs内のデータを基に画面４（予約確定）のHTML出力を行う。
		    	printRsvOKdoc(out, rs);
	    	}else{
	    		printFailDocs(out, "予約できませんでした。aa");
	    	}
	    }catch(Exception e){
	    	//適切なエラー処理。
	    	printFailDocs(out,e.getMessage());
	    }
	}
	//その他の必要なメソッド

	private void printFailDocs(PrintWriter out,String msg){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>画面4　予約確定</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("予約に失敗しました。" + msg);
		out.println("</body>");
		out.println("</html>");
	}
	
	private void printRsvOKdoc(PrintWriter out,ResultSet rs){
		try{
			out.println("<html>");
			out.println("<head>");
			out.println("<title>画面4　予約確定</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>ご予約ありがとうございました。予約内容は以下のとおりです。下記のメールアドレス宛に予約確定メールをお送りしました。</p>");
			out.println("<p>氏名：" + rs.getString("Name") + "</p>");
			out.println("<p>メールアドレス：" + rs.getString("Mail") + "</p>");
			out.println("<p>予約日時：" + rs.getString("rsvDate") + "</p>");
			out.println("<p>担当美容師：" + rs.getString("Biyoshi") + "</p>");
			out.println("<form action=\"testreset\" method=\"get\">");
			out.println("<input type=\"hidden\" name=\"rsvDate\" value=\"" + rs.getString("rsvDate") +"\">");
			out.println("<input type=\"hidden\" name=\"rsvTime\" value=\"" + rs.getString("rsvTime") +"\">");
			out.println("<input type=\"hidden\" name=\"Biyoshi\" value=\"" + rs.getString("Biyoshi") +"\">");
			out.println("<input type=\"submit\" value=\"戻る\"/>");
			out.println("</form>");
			out.println("</body>");
			out.println("</html>");
		}catch(Exception e){
			printFailDocs(out,"エラーが発生しました。" + e.getMessage());
		}
	}
}
