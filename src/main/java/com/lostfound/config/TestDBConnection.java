
package com.lostfound.config;

/**
 *
 * @author Kalana Denuz
 */
public class TestDBConnection {
    public static void main(String[] args) {
        if (DBConnection.getConnection() != null) {
            System.out.println("✅ Connection Successful");
        } else {
            System.out.println("❌ Connection Failed");
        }
    }
}

