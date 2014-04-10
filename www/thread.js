
var ThreadHelper = function() {
};

ThreadHelper.prototype.create = function(delay, successCallback, threadCallback) {
    cordova.exec(
            successCallback, // success callback function
            threadCallback, // error callback function
            'ThreadHelper', // mapped to our native Java class
            'create', // with this action name
            [delay]
            );
};

ThreadHelper.prototype.run = function(id, successCallback, errorCallback) {
    cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'ThreadHelper', // mapped to our native Java class
            'run', // with this action name
            [id]
            );
};

ThreadHelper.prototype.stop = function(id, successCallback, errorCallback) {
    cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'ThreadHelper', // mapped to our native Java class
            'stop', // with this action name
            [id]
            );
};

ThreadHelper.prototype.getState = function(id, successCallback, errorCallback) {
    cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'ThreadHelper', // mapped to our native Java class
            'getState', // with this action name
            [id]
            );
};

ThreadHelper.prototype.remove = function(id, successCallback, errorCallback) {
    cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'ThreadHelper', // mapped to our native Java class
            'remove', // with this action name
            [id]
            );
};

ThreadState = {
    NEW: "NEW",
    BLOCKED: "RUNNING",
    TERMINATED: "TERMINATED"
};

var thread = new ThreadHelper();
module.exports = thread;