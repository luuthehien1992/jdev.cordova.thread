/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdev.cordova.thread;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Jeremy
 */
public class ThreadHelper extends CordovaPlugin {

    private final String TAG = "ThreadHelper";
    private final static List<Thread> threads = new ArrayList<Thread>();
    private final static List<Boolean> runs = new ArrayList<Boolean>();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            Action emAction = Action.fromString(action);

            switch (emAction) {
                case CREATE:
                    create(args.getInt(0), callbackContext);
                    return true;
                case RUN:
                    run(args.getInt(0), callbackContext);
                    return true;
                case STOP:
                    stop(args.getInt(0), callbackContext);
                    return true;
                case REMOVE:
                    remove(args.getInt(0), callbackContext);
                    return true;
                case GET_STATE:
                    getState(args.getInt(0), callbackContext);
                    return true;
                default:
                    return false;
            }
        } catch (JSONException ex) {
            Log.e(TAG, ex.getMessage());
            throw ex;
        }
    }

    private void getState(int threadID, CallbackContext callbackContext) {
        synchronized (threads) {
            if (threadID >= threads.size()) {
                errorCallback(callbackContext, -1);
            } else {
                String state = "Unknown";
                switch (threads.get(threadID).getState()) {
                    case NEW:
                        state = "NEW";
                        break;
                    case BLOCKED:
                        state = "RUNNING";
                        break;
                    case RUNNABLE:
                        state = "RUNNING";
                        break;
                    case TIMED_WAITING:
                        state = "RUNNING";
                        break;
                    case WAITING:
                        state = "RUNNING";
                        break;
                    case TERMINATED:
                        state = "TERMINATED";
                        break;
                }
                successCallback(callbackContext, state);
            }
        }
    }

    private void stop(int threadID, CallbackContext callbackContext) {
        synchronized (runs) {
            if (threadID >= runs.size()) {
                callbackContext.error(-1);
            } else {
                runs.set(threadID, Boolean.FALSE);
            }
        }
        waitStop(threadID);

        callbackContext.success();
    }

    private void waitStop(int threadID) {
        while (true) {
            synchronized (threads) {
                if (!threads.get(threadID).isAlive()) {
                    break;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    private void remove(int threadID, CallbackContext callbackContext) {
        synchronized (threads) {
            synchronized (runs) {
                if (threadID >= threads.size()) {
                    errorCallback(callbackContext, -1);
                } else {
                    threads.remove(threadID);
                    runs.remove(threadID);

                    successCallback(callbackContext);
                }
            }
        }
    }

    private void run(int threadID, CallbackContext callbackContext) {
        synchronized (threads) {
            synchronized (runs) {
                try {
                    if (threadID >= threads.size()) {
                        errorCallback(callbackContext, -1);
                    } else {
                        runs.set(threadID, true);
                        threads.get(threadID).start();

                        successCallback(callbackContext);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());

                    errorCallback(callbackContext, -1);
                }
            }
        }
    }

    private void successCallback(CallbackContext callbackContext, int i) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, i);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void successCallback(CallbackContext callbackContext) {
        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void successCallback(CallbackContext callbackContext, String msg) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, msg);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void errorCallback(CallbackContext callbackContext, int i) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, i);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void errorCallback(CallbackContext callbackContext, String msg) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, msg);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void create(int delay, final CallbackContext callbackContext) {
        int threadID = -1;
        synchronized (threads) {
            synchronized (runs) {
                threadID = runs.size();
                RunnableImp runnableImp = new RunnableImp(threadID, delay, callbackContext);
                Thread thread = new Thread(runnableImp);

                threads.add(thread);
                runs.add(true);
            }
        }
        successCallback(callbackContext, threadID);
    }

    private class RunnableImp implements Runnable {

        private int threadID = 0;
        private int delay = 0;
        private CallbackContext callbackContext = null;

        public RunnableImp(int threadID, int delay, CallbackContext callbackContext) {
            this.threadID = threadID;
            this.delay = delay;
            this.callbackContext = callbackContext;
        }

        public void run() {
            boolean run = true;
            while (run) {
                errorCallback(callbackContext, -1); // use error callback for thread callback

                synchronized (runs) {
                    run = runs.get(threadID);
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
    }

    private enum Action {

        CREATE("create"),
        RUN("run"),
        STOP("stop"),
        GET_STATE("getState"),
        REMOVE("remove");

        private final String statusCode;

        private Action(String s) {
            statusCode = s;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public static Action fromString(String action) {
            if (action != null) {
                for (Action emAction : Action.values()) {
                    if (action.equalsIgnoreCase(emAction.statusCode)) {
                        return emAction;
                    }
                }
            }
            return null;
        }
    }
}
