package udp;

import dto.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Builder;
import utilities.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by wihoho on 19/9/15.
 */
@Data
@Builder
@AllArgsConstructor
public class DHTClient implements Runnable {
    private static final int nodeSize = 100;

    private String id;
    private DatagramSocket socket;
    private Queue<Node> nodes;

    public void findNode(Node destination) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("t", Utils.getRandomString(2));
        map.put("y", "q");
        map.put("q", "find_node");

        Map<String, Object> subMap = new HashMap<>();
        subMap.put("id", id);
        subMap.put("target", Utils.randomId());
        map.put("a", subMap);

        byte[] sendData = Utils.enBencode(map);

        InetAddress destinationIp = InetAddress.getByName(destination.getAddress());
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationIp, destination.getPort());
        socket.send(sendPacket);
    }

    @Override
    public void run() {
        System.out.println("Client starts");
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (nodes) {
                if (nodes.isEmpty()) {
                    continue;
                }

                Node currentNode = nodes.poll();
                try {
                    findNode(currentNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}