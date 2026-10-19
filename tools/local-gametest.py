#!/usr/bin/env python3
from pathlib import Path
import json,os,subprocess,shutil
root=Path(__file__).resolve().parents[1];deps=Path(os.environ.get('NG_CITY_DEPS','/tmp/city-public-deps'));report=json.loads(Path(os.environ.get('NG_CITY_LAUNCH','/tmp/city-launch.json')).read_text());run=root/os.environ.get('NG_CITY_QA_RUN','run-qa');run.mkdir(exist_ok=True)
# Isolate every run: delayed chunk-entity loads must never reuse previous test employees.
world=run/'world'
if world.exists():shutil.rmtree(world)
lib=run/'libraries';lib.mkdir(exist_ok=True)
lookup={p.name.split('-',1)[1]:p for p in (deps/'lib').glob('*')}
def remap(old):
 p=Path(old)
 if p.name in lookup:
  link=lib/p.name
  if link.is_symlink():link.unlink()
  if not link.exists():shutil.copy2(lookup[p.name],link)
  return str(link)
 if old.endswith('/build/resources/main'):return str(root/'build/resources/main')
 if old.endswith('/build/classes/java/main'):return str(root/'build/classes/java/main')
 raise RuntimeError('Missing public dependency: '+p.name)
classpath=[remap(p) for p in report['classpath']]
legacy=(deps/'legacy-classpath.txt').read_text().strip().splitlines()
(run/'classpath.txt').write_text('\n'.join(remap(p) for p in legacy))
props=dict(report['systemProperties']);props['legacyClassPath.file']=str(run/'classpath.txt');props['forge.logging.console.level']='info';props['forge.enabledGameTestNamespaces']=os.environ.get('NG_CITY_TEST_NAMESPACE','ng_city,ng_city_packing,ng_city_packfix,ng_city_shared')
vm=report['jvmArgs'];translated=[]
for arg in vm:
 if arg.startswith('/home/'):
  translated.append(os.pathsep.join(remap(p) for p in arg.split(os.pathsep)))
 else:translated.append(arg)
env=os.environ.copy();env['MOD_CLASSES']='ng_city%%'+str(root/'build/resources/main')+os.pathsep+'ng_city%%'+str(root/'build/classes/java/main')
cmd=['java','-Xmx4G','-XX:ActiveProcessorCount=4']+translated+['-D'+str(k)+'='+str(v) for k,v in props.items()]+['-cp',os.pathsep.join(classpath),report['main']]+report['args']
(run/'launch-command.json').write_text(json.dumps(cmd,indent=2));(run/'eula.txt').write_text('eula=true\n')
result=subprocess.run(cmd,cwd=run,env=env);sys_exit=result.returncode
raise SystemExit(sys_exit)
