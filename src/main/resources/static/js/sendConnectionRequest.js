import * as util from "./util.js";

export default function sendConnectionRequest(id, stompClient) {
    console.log(`sending connection request to ${id}`);
    let connectionStatus = {
        "userId": id,
        "status": util.CONNECTION_REQUEST
    }
    stompClient.publish({
        destination: '/app/requestConnection',
        body: JSON.stringify(connectionStatus)
    });
}