import subprocess
import os
import os.path
import sys
import shutil

#
# This is a script for cleaning up and setting up the 2 clusters 
#

if __name__ == '__main__':
    if 'GEMFIRE' not in os.environ:
        sys.exit('please set the GEMFIRE environment variable')

    #### CONSTANTS
    here = os.path.dirname(os.path.abspath(sys.argv[0]))
    gfsh = os.path.join(os.environ['GEMFIRE'],'bin','gfsh')
    cluster_dirs = ['cluster_a', 'cluster_b']
    cluster_cmd = ['python', 'cluster.py']
    locator_ports = {'cluster_a': 10000, 'cluster_b': 20000}

    #### STOP CLUSTERS, REMOVE the DIRS
    for d in cluster_dirs:
        subprocess.check_call(['python','cluster.py','stop'], cwd = os.path.join(here,d))
        for subd in ['loctor1','datanode1','datanode2']:
            shutil.rmtree(os.path.join(here,d,subd),True)

    #### START LOCATORS, IMPORT STARTERS, START CLUSTER
    for d in cluster_dirs:
        subprocess.check_call(cluster_cmd + ['start','locator1'], cwd = os.path.join(here,d))
        connect = 'connect --locator=localhost[{0}]'.format(locator_ports[d])
        importcmd = 'import cluster-configuration --zip-file-name={0}.zip'.format(d)
        subprocess.check_call([gfsh,'-e',connect,'-e',importcmd])
        subprocess.check_call(cluster_cmd + ['start'], cwd = os.path.join(here,d))

    #### BUILD AND DEPLOY THE FUNCTIONS JAR TO CLUSTER B
    function_jar = os.path.join(here,'functions','target','functions-1.0-SNAPSHOT.jar')
    subprocess.check_call(['mvn','package'],cwd=os.path.join(here,'functions'))
    subprocess.check_call([gfsh,'-e','connect --locator=localhost[20000]','-e','deploy --jar={}'.format(function_jar),'-e','list functions'])

    create_sequence_region_cmd = 'create region --name=sequence --type=PARTITION_REDUNDANT --cache-loader=io.pivotal.pde.sample.SequenceCacheLoader'
    subprocess.check_call([gfsh,'-e','connect --locator=localhost[20000]','-e',create_sequence_region_cmd, '-e', 'list regions'])
