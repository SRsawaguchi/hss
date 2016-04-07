package jp.co.u_sha.hssystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestReset extends HttpServlet{

	private static final long serialVersionUID = 6566667876723540597L;

	static final int RSV_AKI = 0;
	static final int RSV_KARI = 1;
	static final int RSV_KAKUTEI = 2;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn;
		PreparedStatement psUp;
		response.setContentType("text/html; charset=Shift_JIS");
		PrintWriter out = response.getWriter();
		
	    String url = "jdbc:mysql://localhost/hss";
	    String user = "testuser";
	    String pass = "testpass";
	    try{
	    	Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	conn = DriverManager.getConnection(url, user, pass);
	    	String rsvDate = request.getParameter("rsvDate");
	    	String rsvTime = request.getParameter("rsvTime");
	    	String biyoshi = request.getParameter("Biyoshi");
	    	psUp = conn.prepareStatement("UPDATE rsvlist SET Status=?, LastUpdate=?, CustID=? WHERE rsvDate=? AND rsvTime=? AND Biyoshi=?");
	    	psUp.setInt(1,RSV_KARI); psUp.setLong(2, 0); psUp.setString(3, "");
	    	psUp.setString(4,rsvDate); psUp.setString(5, rsvTime); psUp.setString(6, biyoshi);
	    	psUp.executeUpdate();
	    	request.getSession().invalidate();
	    	printMessage(out, "DBをリセットしました。" + "セッションを切断しました。");
	    }catch(Exception e){
	    	printMessage(out, "エラーが発生しました。" + e.getMessage());
	    }
	}
	
	private void printMessage(PrintWriter out,String msg){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>テストのリセット</title>");
		out.println("</head>");
		out.println("<body>");
		out.println(msg);
		out.println("<a href=\"login\">ログイン画面へ戻る</a>");
		out.println("</body>");
		out.println("</html>");
	}

	
}
