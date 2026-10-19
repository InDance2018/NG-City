#!/usr/bin/env python3
"""Offline compilation using an existing, verified Forge 48.1.0 dependency set.
Normal developers should use ./gradlew build. Dependencies are never packaged.
"""
from pathlib import Path
import subprocess,sys,shutil,zipfile,json,os
ROOT=Path(__file__).resolve().parents[1]
DEPS=Path(sys.argv[1] if len(sys.argv)>1 else '/tmp/city-public-deps')
MODE=sys.argv[2] if len(sys.argv)>2 else 'compile'
libs=list((DEPS/'lib').glob('*.jar'))
# Jar file prefixes only distinguish cached duplicate dependencies.
def basename(p):return p.name.split('-',1)[1]
byname={basename(p):p for p in libs}
# Prefer the exact versions used by the existing Forge 48.1.0 build.
launch=Path(os.environ.get('NG_CITY_LAUNCH','/tmp/city-launch.json'))
if launch.exists():
 report=json.loads(launch.read_text());wanted=[Path(p).name for p in report['classpath'] if p.endswith('.jar')]
 cp=[byname[n] for n in wanted if n in byname]
 # Public tools may not be in the game runtime classpath.
else:cp=libs
classes=ROOT/'build/classes/java/main'
if MODE!='package' and classes.exists():shutil.rmtree(classes)
classes.mkdir(parents=True,exist_ok=True)
resources=ROOT/'build/resources/main'
if MODE!='package':
 if resources.exists():shutil.rmtree(resources)
 shutil.copytree(ROOT/'src/main/resources',resources)
sources=sorted((ROOT/'src/main/java').rglob('*.java'))
if MODE=='qa':
 sources+=sorted((ROOT/'src/qa/java').rglob('*.java'))
 shutil.copytree(ROOT/'src/qa/resources',resources,dirs_exist_ok=True)
args=['-proc:none','-encoding','UTF-8','--release','17','-classpath',':'.join(map(str,cp)),'-d',str(classes)]+[str(s) for s in sources]
argfile=ROOT/'build/javac.args';argfile.write_text('\n'.join('"'+a+'"' for a in args))
if MODE!='package':subprocess.run(['java','-XX:ActiveProcessorCount=4','com.sun.tools.javac.Main','@'+str(argfile)],check=True)
print('Using previously tested classes' if MODE=='package' else 'Compiled '+str(len(sources))+' sources',flush=True)
if MODE=='qa':sys.exit(0)
unmapped=ROOT/'build/ng-city-dev2-unmapped.jar'
with zipfile.ZipFile(unmapped,'w',zipfile.ZIP_DEFLATED) as z:
 z.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nImplementation-Version: 0.0.2-dev\n\n')
 for folder in [classes,resources]:
  for p in sorted(folder.rglob('*')):
   if p.is_file() and 'GameTests' not in p.name and not (folder==resources and (ROOT/'src/qa/resources'/p.relative_to(folder)).is_file()):z.write(p,p.relative_to(folder))
output=ROOT/'build/libs/ng_city-1.20.2-dev-0.0.2.jar';output.parent.mkdir(exist_ok=True)
fart=next(p for p in libs if 'ForgeAutoRenamingTool' in p.name)
tool_libs=[fart,byname["jopt-simple-5.0.4.jar"]]+[p for p in libs if p.name.split('-',1)[1].startswith('asm') and '-9.7.1.jar' in p.name]+[p for p in libs if 'srgutils-0.5.10' in p.name]+libs
cmd=['java','-Xmx2G','-XX:ActiveProcessorCount=4','-cp',':'.join(map(str,tool_libs)),'net.minecraftforge.fart.Main','--input',str(unmapped),'--output',str(output),'--names',str(DEPS/'reobf-mappings.tsrg')]
for p in cp:cmd+=['--lib',str(p)]
subprocess.run(cmd,check=True)
print(output)
