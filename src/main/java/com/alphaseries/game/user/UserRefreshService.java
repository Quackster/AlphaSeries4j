package com.alphaseries.game.user;

import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.server.mus.MusConnectionManager;

import java.sql.SQLException;

public final class UserRefreshService {
    private UserRefreshService() {
    }

    public static String creditsRefreshPayload(long creditsValue) {
        return UserPayloads.creditsRefresh(creditsValue);
    }

    public static String activityPointRefreshPayload(long pointType, long pointsValue) {
        return UserPayloads.activityPointRefresh(pointType, pointsValue);
    }

    public static String activityPointRefreshPayloads(long... pointValues) {
        return UserPayloads.activityPointRefreshes(pointValues);
    }

    /**
     * Original function: Proc_10_16_80C480.
     */
    public static long sendCreditsRefresh(long userId) {
        try {
            if (userId <= 0L) {
                return 0L;
            }
            long socketIndex = SessionState.instance().linkedSocketIndex(userId);
            if (socketIndex == 0L) {
                return 0L;
            }
            long creditsValue = userDao().credits(userId);
            MusConnectionManager.instance().sendData((int) socketIndex, creditsRefreshPayload(creditsValue));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_10_17_80C6B0.
     */
    public static long sendActivityPointRefreshes(long userId) {
        try {
            if (userId <= 0L) {
                return 0L;
            }
            long socketIndex = SessionState.instance().linkedSocketIndex(userId);
            if (socketIndex == 0L) {
                return 0L;
            }
            UserDao users = userDao();
            long sentCount = 0L;
            for (long pointType = 0L; pointType <= 4L; pointType++) {
                long pointsValue = users.activityPoints(userId, pointType);
                MusConnectionManager.instance().sendData((int) socketIndex,
                    activityPointRefreshPayload(pointType, pointsValue));
                sentCount++;
            }
            return sentCount;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String emailValidatedPayload(long emailState) {
        return UserPayloads.emailValidated(emailState);
    }

    /**
     * Original function: Proc_10_19_80CCD0.
     */
    public static long validateEmailAndRefresh(long userId) {
        try {
            if (userId <= 0L) {
                return 0L;
            }
            UserDao userDao = userDao();
            userDao.markEmailValidated(userId);
            long socketIndex = SessionState.instance().linkedSocketIndex(userId);
            if (socketIndex <= 0L) {
                return 0L;
            }
            long emailState = userDao.emailValidated(userId);
            MusConnectionManager.instance().sendData((int) socketIndex, emailValidatedPayload(emailState));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String userIdentityRefreshPayload(long userId, String mottoText, String figureText, String genderText) {
        return UserPayloads.identityRefresh(userId, mottoText, figureText, genderText);
    }

    /**
     * Original function: Proc_10_22_80D460.
     */
    public static long sendUserIdentityRefresh(long requestedUserId) {
        try {
            if (requestedUserId <= 0L) {
                return 0L;
            }
            UserDao.UserIdentity identity = userDao().findIdentity(requestedUserId).orElse(null);
            if (identity == null) {
                return 0L;
            }
            long userId = identity.userId();
            long socketIndex = identity.socketIndex();
            if (socketIndex <= 0L) {
                socketIndex = SessionState.instance().linkedSocketIndex(userId);
            }
            if (socketIndex <= 0L) {
                return 0L;
            }
            MusConnectionManager.instance().sendData((int) socketIndex,
                userIdentityRefreshPayload(userId, identity.motto(), identity.figure(), identity.gender()));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    private static UserDao userDao() throws SQLException {
        return new UserDao(configuredDatabase());
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }
}
