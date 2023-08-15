package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {
	private Connection conn;
	private ResultSet rs;
	
	public BbsDAO() {
		try {
			String dbURL = "jdbc:oracle:thin:@localhost:1521:XE"; // 이 부분 수정
			String dbID = "BBS";
			String dbPassword = "1234";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public java.sql.Timestamp getDate() {
	    String SQL = "SELECT SYSDATE FROM DUAL"; // Oracle에서 현재 날짜와 시간 가져오는 방법
	    try {
	        PreparedStatement pstmt = conn.prepareStatement(SQL);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getTimestamp(1); // 현재 날짜와 시간을 Timestamp 형태로 반환
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null; // 데이터베이스 오류
	}

	public int getNext() {
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) + 1;
			}
			return 1; //첫 번째 게시물인 경우
		}catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	
	public int write(String bbsTitle, String userID, String bbsContent) {
		String SQL = "INSERT INTO BBS VALUES (?,?,?,?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setTimestamp(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	
	public ArrayList<Bbs> getList(int pageNumber) {
		String SQL = "SELECT * FROM (SELECT * FROM bbs WHERE bbsID < ? and bbsAvailable = 1 order by bbsID desc) WHERE ROWNUM <=10";
		ArrayList<Bbs> list = new ArrayList<Bbs>();    
		try {
		        PreparedStatement pstmt = conn.prepareStatement(SQL);
		        pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	Bbs bbs = new Bbs();
		        	bbs.setBbsID(rs.getInt(1));
		        	bbs.setBbsTitle(rs.getString(2));
		        	bbs.setUserID(rs.getString(3));
		        	bbs.setBbsDate(rs.getString(4));
		        	bbs.setBbsContent(rs.getString(5));
		        	bbs.setBbsAvailable(rs.getInt(6));
		            list.add(bbs);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return list;
		}
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM (SELECT * FROM bbs WHERE bbsID < ? and bbsAvailable = 1 order by bbsID desc) WHERE ROWNUM <=10";  
		try {
		        PreparedStatement pstmt = conn.prepareStatement(SQL);
		        pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
		        rs = pstmt.executeQuery();
		        if(rs.next()) {
		        	return true;
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return false;
	}
	
	public Bbs getBbs(int bbsID) {
		String SQL = "SELECT * FROM BBS WHERE bbsID = ?";  
		try {
		        PreparedStatement pstmt = conn.prepareStatement(SQL);
		        pstmt.setInt(1,bbsID);
		        rs = pstmt.executeQuery();
		        if(rs.next()) {
		        	Bbs bbs = new Bbs();
		        	bbs.setBbsID(rs.getInt(1));
		        	bbs.setBbsTitle(rs.getString(2));
		        	bbs.setUserID(rs.getString(3));
		        	bbs.setBbsDate(rs.getString(4));
		        	bbs.setBbsContent(rs.getString(5));
		        	bbs.setBbsAvailable(rs.getInt(6));
		        	return bbs;
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return null;
	}
	public int update(int bbsID, String bbsTitle, String bbsContent) {
		String SQL = "UPDATE BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTitle);
			pstmt.setString(2, bbsContent);
			pstmt.setInt(3, bbsID);
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	public int delete(int bbsID) {
		String SQL = "UPDATE BBS SET bbsAvailable = 0 WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
}
