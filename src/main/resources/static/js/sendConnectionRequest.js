export default function sendConnectionRequest(id, stompClient) {
    console.log(`sending connection request to ${id}`);
    let connectionStatus = {
        "userId": id,
        "status": "connectionRequest"
    }
    stompClient.publish({
        destination: '/app/requestConnection',
        body: JSON.stringify(connectionStatus)
    });
}