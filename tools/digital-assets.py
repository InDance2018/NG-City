#!/usr/bin/env python3
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
import json
r=Path(__file__).resolve().parents[1]/'src/main/resources/assets/ng_city'
def texture(name,base,draw):
 im=Image.new('RGB',(128,128),base);d=ImageDraw.Draw(im);draw(d);im.save(r/'textures/block'/f'{name}.png')
f=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',10)
texture('atm_metal',(73,85,98),lambda d:[d.line((0,y,127,y),fill=(79,91,104)) for y in range(0,128,4)])
texture('atm_screen',(8,23,37),lambda d:(d.rectangle((4,4,123,123),outline=(39,190,193),width=2),d.text((12,12),'NG BANK',font=f,fill=(76,232,218)),d.line((12,30,115,30),fill=(50,90,112),width=2),d.text((12,42),'GELDAUTOMAT',font=f,fill='white'),d.text((12,64),'Karte einlegen',font=f,fill=(159,192,210)),d.rounded_rectangle((12,89,115,114),4,fill=(27,101,113)),d.text((29,96),'START',font=f,fill='white')))
texture('atm_keypad',(26,37,48),lambda d:[d.rounded_rectangle((12+x*29,9+y*29,35+x*29,31+y*29),3,fill=((52,181,131) if x==3 and y==3 else (174,73,65) if x==3 and y==0 else (164,177,186))) for x in range(4) for y in range(4)])
texture('atm_reader',(20,31,43),lambda d:(d.rectangle((8,42,120,83),fill=(7,13,20)),d.rectangle((12,46,115,53),fill=(46,229,178)),d.text((22,11),'KARTE',font=f,fill=(184,207,219))))
texture('atm_cash',(36,47,59),lambda d:(d.text((34,15),'GELD',font=f,fill=(205,217,225)),d.rectangle((10,46,117,88),fill=(4,10,16)),d.line((12,88,115,88),fill=(46,229,178),width=4)))
def cube(a,b,tex):
 return {'from':a,'to':b,'faces':{face:{'texture':'#'+tex,'uv':[0,0,16,16]} for face in ['north','south','east','west','up','down']}}
def front(a,b,tex):
 e=cube(a,b,'metal');e['faces']['north']={'texture':'#'+tex,'uv':[0,0,16,16]};return e
textures={k:'ng_city:block/atm_'+v for k,v in {'metal':'metal','screen':'screen','keys':'keypad','reader':'reader','cash':'cash'}.items()}
textures['particle']='#metal'
e=[cube([1,0,3],[15,2,15],'metal'),cube([2,2,5],[14,16,15],'metal'),cube([1,15,2],[15,16,15],'metal'),front([3,9,3],[10.5,14.5,5],'screen'),front([11,9.5,2.8],[13.5,11.7,5],'reader'),front([3,3.5,3],[13,6,5],'cash')]
k=cube([3,7,1],[13,8,7],'keys');e.append(k)
model={'textures':textures,'elements':e,'display':{'gui':{'rotation':[25,225,0],'scale':[0.85,0.85,0.85]},'ground':{'translation':[0,3,0],'scale':[0.5,0.5,0.5]}}}
(r/'models/block/geldautomat.json').write_text(json.dumps(model,indent=2))
t={'textures':textures,'elements':[cube([2,0,4],[14,2,13],'metal'),cube([6,2,8],[10,7,11],'metal'),front([2,7,5],[14,15,8],'screen'),cube([3,2,1],[13,3,7],'keys'),front([11,4,4],[14,6,7],'reader')],'display':model['display']}
(r/'models/block/zahlungsterminal.json').write_text(json.dumps(t,indent=2))
