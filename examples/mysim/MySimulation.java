package mysim;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class MySimulation {

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

        broker.submitVmList(vmList);

        // 5. Create Cloudlets
        List<Cloudlet> cloudletList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int cloudletId = i;
            long length = 400000;
            long fileSize = 300;
            long outputSize = 300;
            int pesNumber = 1;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet = new Cloudlet(cloudletId, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudlet.setVmId(i);
            cloudletList.add(cloudlet);
        }

        broker.submitCloudletList(cloudletList);

        // 6. Start the simulation
        CloudSim.startSimulation();

        // 7. Stop the simulation
        CloudSim.stopSimulation();

        // 8. Print results
        List<Cloudlet> newList = broker.getCloudletReceivedList();
        printCloudletList(newList);

        Log.printLine("Simulation finished!");
    }

    private static Datacenter createDatacenter(String name) {
        // Create list to store machine
        List<Host> hostList = new ArrayList<>();

        // Create Host
        int mips = 1000;
        int hostId = 0;
        int ram = 2048;
        long storage = 1000000;
        int bw = 10000;

        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        hostList.add(
            new Host(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
            )
        );

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw
        );

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}

            