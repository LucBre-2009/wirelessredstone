from pathlib import Path
from PIL import Image

ROOT = Path("src/main/resources/assets/wirelessredstone/textures/block")
ROOT.mkdir(parents=True, exist_ok=True)

DARK=(40,43,42,255); STEEL=(93,100,98,255); STEEL_L=(145,151,145,255)
STEEL_H=(184,188,178,255); WOOD=(105,72,40,255); WOOD_L=(139,99,56,255)
WOOD_D=(72,49,31,255); QUARTZ=(219,216,205,255); QUARTZ_L=(242,239,229,255)
QUARTZ_D=(166,163,154,255); RED=(145,22,25,255); RED_L=(204,44,49,255)
BLUE=(49,78,173,255); BLUE_L=(83,116,215,255); BLACK=(25,26,25,255)

def save(name, pixels):
    im=Image.new("RGBA",(16,16))
    im.putdata([pixels[y][x] for y in range(16) for x in range(16)])
    im.save(ROOT/f"{name}.png")

def panel(material):
    p=[[material for _ in range(16)] for _ in range(16)]
    for y in range(16):
        for x in range(16):
            if x in (0,15) or y in (0,15): p[y][x]=STEEL
            elif x in (1,14) or y in (1,14): p[y][x]=STEEL_H if (x+y)%2 else STEEL_L
            elif x in (2,13) or y in (2,13): p[y][x]=WOOD_D if material==WOOD else (166,163,154,255)
            elif material==WOOD: p[y][x]=WOOD_L if (x+y)%5==0 else WOOD_D if (x*3+y)%7==0 else WOOD
            else: p[y][x]=QUARTZ_L if (x+y)%5==0 else (166,163,154,255) if (x*3+y)%7==0 else QUARTZ
    return p

# Receiver's wood panel and sender's matching panel with only the wood replaced by quartz.
save("wireless_receiver_top",panel(WOOD))
save("wireless_sender_top",panel(QUARTZ))

def side(material):
    p=[[DARK for _ in range(16)] for _ in range(16)]
    for y in range(16):
        for x in range(16):
            if y in (0,1,14,15): p[y][x]=STEEL_H if x%3 else STEEL_L
            elif y in (2,3,12,13): p[y][x]=STEEL
            else:
                if material==WOOD: p[y][x]=WOOD_L if (x+y)%5==0 else WOOD_D if (x+y)%3==0 else WOOD
                else: p[y][x]=QUARTZ_L if (x+y)%5==0 else (166,163,154,255) if (x+y)%3==0 else QUARTZ
    for y in range(16): p[y][0]=STEEL; p[y][15]=STEEL
    return p
save("wireless_receiver_side",side(WOOD))
save("wireless_sender_side",side(QUARTZ))

for name,color,highlight in [("wireless_button_blue",BLUE,BLUE_L),("wireless_button_red",RED,RED_L)]:
    p=[[STEEL for _ in range(16)] for _ in range(16)]
    for y in range(16):
        for x in range(16):
            if x in (0,15) or y in (0,15): p[y][x]=STEEL_L
            elif x in (1,14) or y in (1,14): p[y][x]=STEEL_H
            elif x in (2,13) or y in (2,13): p[y][x]=DARK
            elif 3<=x<=12 and 3<=y<=12: p[y][x]=color
    for y in range(3,7):
        for x in range(3,8): p[y][x]=highlight
    for x in range(4,12): p[12][x]=DARK
    save(name,p)

def antenna(shaft, name):
    p=[[shaft for _ in range(16)] for _ in range(16)]
    for y in range(16):
        for x in range(16):
            if x in (0,15,13,14): p[y][x]=BLACK
            elif x in (1,2): p[y][x]=QUARTZ_L if shaft==QUARTZ else WOOD_L
            elif y<=2: p[y][x]=RED_L if x in (6,7,8,9) and y==1 else RED
            else: p[y][x]=shaft
    save(name,p)
antenna(WOOD_D,"wireless_antenna")
antenna(QUARTZ_D,"wireless_sender_antenna")
save("wireless_receiver_indicator_off",[[WOOD_D if (x+y)%3 else RED for x in range(16)] for y in range(16)])
save("wireless_receiver_indicator_on",[[RED_L if (x+y)%3 else RED for x in range(16)] for y in range(16)])
print("Generated matching wood receiver and quartz sender textures.")
