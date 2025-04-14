package mysim;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class CICDSimulation {

    public static void main(String[] args) {

        // 1. Initialize CloudSim
        int numUsers = 1;
        Calendar calendar = Calendar.getInstance();
        boolean traceFlag = false;
        CloudSim.init(numUsers, calendar, traceFlag);

        // 2. Create Datacenter (CI/CD Agent Host)
        Datacenter datacenter = createDatacenter("Datacenter_1");

        // 3. Create Broker
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int brokerId = broker.getId();

        // 4. Create VMs
        List<Vm> vmList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int vmId = i;
            int mips = 1000;
            long size = 10000;
            int ram = 512;
            long bw = 1000;
            int pesNumber = 1;
            String vmm = "Xen";

            Vm vm = new Vm(vmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }

        // 5. Create Cloudlets
        List<Cloudlet> cloudletList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int cloudletId = i;

            