import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;

import java.util.ArrayList;
import java.util.List;

public class parameters {
    public static void main(String[] args) {
        // Initialize CloudSim
        CloudSim.init(1, Calendar.getInstance(), false);

        // Providers and their data centers
        List<DataCenter> provider1DataCenters = createDataCentersForProvider(new CostModelProvider1());
        List<DataCenter> provider2DataCenters = createDataCentersForProvider(new CostModelProvider2());
        List<DataCenter> provider3DataCenters = createDataCentersForProvider(new CostModelProvider3());

        // Run the simulation (your simulation logic here)

        CloudSim.terminateSimulation();
    }

    private static List<DataCenter> createDataCentersForProvider(CostModel costModel) {
        List<DataCenter> dataCenters = new ArrayList<>();
        int numDataCenters = new Random().nextInt(4) + 2; // Random number between 2 and 5

        for (int i = 0; i < numDataCenters; i++) {
            String region = assignRegionRandomly();
            DataCenter dataCenter = createDataCenter(region, costModel);
            dataCenters.add(dataCenter);
        }

        return dataCenters;
    }

    private static String assignRegionRandomly() {
        String[] regions = {"US", "EU", "AS"};
        return regions[new Random().nextInt(regions.length)];
    }

    private static Datacenter createDataCenter(String region, CostModel costModel) {
        // Create a list to store hosts in a Datacenter
        List<Host> hostList = new ArrayList<Host>();
    
        int numHosts = 8; // Number of VMs within a DC
        for (int hostId = 0; hostId < numHosts; hostId++) {
            List<Pe> peList = new ArrayList<Pe>();
    
            int mips = 1500; // VM processing capability
            int numPes = 2; // VM number of CPU
            for (int peId = 0; peId < numPes; peId++) {
                peList.add(new Pe(peId, new PeProvisionerSimple(mips))); // Create PEs for the Host
            }
    
            int ram = 4096; // VM RAM in MB
            long storage = 8000; // VM storage capacity in MB
            int bw = 10000; // Intra-DC BW in Mb/s
    
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
        }
    
        // Define Datacenter characteristics
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double costPerSec = costModel.getCostPerCPU(region);
        double costPerMem = 0.5; // cost of using memory in this resource
        double costPerStorage = costModel.getCostPerStorage(region);
        double costPerBw = costModel.getCostPerBandwidth(region, false);
    
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, time_zone, costPerSec, costPerMem, costPerStorage, costPerBw);
    
        // Create the Datacenter
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return datacenter;
    }

    static class CostModelProvider1 implements CostModel {
        // Pricing for Provider 1
        private final double cpuPriceUS = 0.020;
        private final double cpuPriceEU = 0.025;
        private final double cpuPriceAS = 0.027;
        private final double storagePriceUS = 0.006;
        private final double storagePriceEU = 0.006;
        private final double storagePriceAS = 0.0066;
        private final double bwPriceIntraDC = 0.001;
        private final double bwPriceUS = 0.0015;
        private final double bwPriceEU = 0.002;
        private final double bwPriceAS = 0.004;
        private final double bwPriceInterRegions = 0.008;

        @Override
        public double getCostPerCPU(String region) {
            switch (region) {
                case "US":
                    return cpuPriceUS;
                case "EU":
                    return cpuPriceEU;
                case "AS":
                    return cpuPriceAS;
                default:
                    return 0; // Default or throw an exception for unknown region
            }
        }

        @Override
        public double getCostPerStorage(String region) {
            switch (region) {
                case "US":
                    return storagePriceUS;
                case "EU":
                    return storagePriceEU;
                case "AS":
                    return storagePriceAS;
                default:
                    return 0; // Default or throw an exception for unknown region
            }
        }

        @Override
        public double getCostPerBandwidth(String region, boolean interRegion) {
            if (interRegion) {
                return bwPriceInterRegions;
            } else {
                switch (region) {
                    case "US":
                        return bwPriceUS;
                    case "EU":
                        return bwPriceEU;
                    case "AS":
                        return bwPriceAS;
                    default:
                        return bwPriceIntraDC; // Default or throw an exception for unknown region
                }
            }
        }
    }

    interface CostModel {
        double getCostPerCPU(String region);
        double getCostPerStorage(String region);
        double getCostPerBandwidth(String region, boolean interRegion);
    }

    static class CostModelProvider2 implements CostModel {
        // Pricing for Provider 2
        private final double cpuPriceUS = 0.020;
        private final double cpuPriceEU = 0.018;
        private final double cpuPriceAS = 0.020;
        private final double storagePriceUS = 0.0096;
        private final double storagePriceEU = 0.008;
        private final double storagePriceAS = 0.0096;
        private final double bwPriceIntraDC = 0.001;
        private final double bwPriceUS = 0.0015;
        private final double bwPriceEU = 0.002;
        private final double bwPriceAS = 0.004;
        private final double bwPriceInterRegions = 0.008;

        @Override
        public double getCostPerCPU(String region) {
            switch (region) {
                case "US":
                    return cpuPriceUS;
                case "EU":
                    return cpuPriceEU;
                case "AS":
                    return cpuPriceAS;
                default:
                    return 0; // Default or throw an exception for unknown region
            }
        }

        @Override
        public double getCostPerStorage(String region) {
            switch (region) {
                case "US":
                    return storagePriceUS;
                case "EU":
                    return storagePriceEU;
                case "AS":
                    return storagePriceAS;
                default:
                    return 0; // Default or throw an exception for unknown region
            }
        }

        @Override
        public double getCostPerBandwidth(String region, boolean interRegion) {
            if (interRegion) {
                return bwPriceInterRegions;
            } else {
                switch (region) {
                    case "US":
                        return bwPriceUS;
                    case "EU":
                        return bwPriceEU;
                    case "AS":
                        return bwPriceAS;
                    default:
                        return bwPriceIntraDC; // Default or throw an exception for unknown region
                }
            }
        }
    }

    interface CostModel {
        double getCostPerCPU(String region);
        double getCostPerStorage(String region);
        double getCostPerBandwidth(String region, boolean interRegion);
    }

    static class CostModelProvider3 implements CostModel {
        // Pricing for Provider 3
        private final double cpuPriceUS = 0.0095;
        private final double cpuPriceEU = 0.0090;
        private final double cpuPriceAS = 0.0080;
        private final double storagePriceUS = 0.00120;
        private final double storagePriceEU = 0.0096;
        private final double storagePriceAS = 0.0090;
        private final double bwPriceIntraDC = 0.001;
        private final double bwPriceUS = 0.0015;
        private final double bwPriceEU = 0.002;
        private final double bwPriceAS = 0.004;
        private final double bwPriceInterRegions = 0.008;

        @Override
        public double getCostPerCPU(String region) {
            switch (region) {
                case "US":
                    return cpuPriceUS;
                case "EU":
                    return cpuPriceEU;
                case "AS":
                    return cpuPriceAS;
                default:
                    return 0; // Default or throw an exception for unknown region
            }
        }

        @Override
        public double getCostPerStorage(String region) {
            switch (region) {
                case "US":
                    return storagePriceUS;
                case "EU":
                    return storagePriceEU;
                case "AS":
                    return storagePriceAS;
                default:
                    return 0; // Default or throw an exception for unknown region
            }
        }

        @Override
        public double getCostPerBandwidth(String region, boolean interRegion) {
            if (interRegion) {
                return bwPriceInterRegions;
            } else {
                switch (region) {
                    case "US":
                        return bwPriceUS;
                    case "EU":
                        return bwPriceEU;
                    case "AS":
                        return bwPriceAS;
                    default:
                        return bwPriceIntraDC; // Default or throw an exception for unknown region
                }
            }
        }
    }

    interface CostModel {
        double getCostPerCPU(String region);
        double getCostPerStorage(String region);
        double getCostPerBandwidth(String region, boolean interRegion);
    }

