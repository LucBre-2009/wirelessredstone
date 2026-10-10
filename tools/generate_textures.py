from pathlib import Path
from PIL import Image

ROOT = Path("src/main/resources/assets/wirelessredstone/textures/block")
ROOT.mkdir(parents=True, exist_ok=True)

DARK=(35,39,46,255); MID=(66,73,82,255); LIGHT=(112,121,132,255); HI=(160,169,177,255)
COPPER=(177,91,48,255); COPPER_L=(222,132,75,255); COPPER_D=(111,53,34,255)
RED=(178,22,28,255); RED_L=(255,57,52,255); RED_D=(94,10,18,255)
WHITE=(208,211,207,255); WARM=(160,164,159,255); BLACK=(22,24,28,255)

def save(name, pixels):
    image = Image.new("RGBA", (16, 16))
    image.putdata([pixels[y][x] for y in range(16) for x in range(16)])
    image.save(ROOT / f"{name}.png")

def canvas(color):
    return [[color for _ in range(16)] for _ in range(16)]

p=canvas(DARK)
for y in range(16):
    for x in range(16):
        if x in (0,15) or y in (0,15): p[y][x]=LIGHT
        elif x in (1,14) or y in (1,14): p[y][x]=MID
        else: p[y][x]=DARK if ((x//3+y//3)%3) else (43,48,55,255)
for y in range(4,12):
    for x in range(4,12): p[y][x]=MID if x in (4,11) or y in (4,11) else (49,54,61,255)
for x,y in [(2,2),(13,2),(2,13),(13,13)]: p[y][x]=HI
for x,y in [(7,6),(8,6),(7,7),(8,7),(7,8),(8,8)]: p[y][x]=RED_L if (x+y)%2 else RED
save("wireless_sender_casing",p)

p=canvas(DARK)
for y in range(16):
    for x in range(16):
        edge=min(x,y,15-x,15-y)
        p[y][x] = COPPER_D if edge==0 else COPPER_L if edge==1 else DARK if edge==2 else COPPER if edge==3 else COPPER_D if edge==4 else DARK
for y in range(6,10):
    for x in range(6,10): p[y][x]=RED_L if x in (7,8) and y in (7,8) else RED
p[6][6]=COPPER_L; p[6][9]=COPPER_L; p[9][6]=COPPER; p[9][9]=COPPER
save("wireless_sender_coil",p)

p=canvas(BLACK)
for y in range(16):
    for x in range(16):
        if x in (0,15) or y in (0,15): p[y][x]=LIGHT
        elif x in (1,14) or y in (1,14): p[y][x]=MID
        elif 3<=x<=12 and 3<=y<=12: p[y][x]=RED_D
        else: p[y][x]=DARK
for y in range(5,11):
    for x in range(5,11): p[y][x]=RED_L if (x+y)%3==0 else RED
for x,y in [(6,6),(7,6),(6,7)]: p[y][x]=(255,112,93,255)
save("wireless_sender_core",p)

p=canvas(WARM)
for y in range(16):
    for x in range(16):
        if x in (0,15) or y in (0,15): p[y][x]=HI
        elif x in (1,14) or y in (1,14): p[y][x]=WHITE
        elif 3<=x<=12 and 3<=y<=12: p[y][x]=MID if x in (3,12) or y in (3,12) else DARK
        else: p[y][x]=WARM
for x,y in [(2,2),(13,2),(2,13),(13,13)]: p[y][x]=COPPER_L
save("wireless_receiver_casing",p)

p=canvas(DARK)
for y in range(16):
    for x in range(16):
        edge=min(x,y,15-x,15-y)
        p[y][x] = COPPER_D if edge==0 else COPPER_L if edge==1 else COPPER if edge==2 else DARK if edge==3 else COPPER_D if edge==4 else (MID if (x+y)%4==0 else DARK)
for x,y in [(5,5),(10,5),(5,10),(10,10)]: p[y][x]=COPPER_L
save("wireless_receiver_trim",p)

for name,on in [("wireless_receiver_indicator_off",False),("wireless_receiver_indicator_on",True)]:
    p=canvas(DARK)
    for y in range(16):
        for x in range(16):
            if x in (0,15) or y in (0,15): p[y][x]=LIGHT
            elif x in (1,14) or y in (1,14): p[y][x]=MID
            elif 3<=x<=12 and 4<=y<=11: p[y][x]=BLACK
            else: p[y][x]=DARK
    for y in range(5,11):
        for x in range(4,12): p[y][x]=(RED_L if (x+y)%4==0 else RED) if on else (COPPER if (x+y)%3==0 else COPPER_D)
    if on:
        for x,y in [(5,5),(6,5),(5,6)]: p[y][x]=(255,137,116,255)
    save(name,p)

print(f"Generated 7 custom 16x16 textures in {ROOT}")
