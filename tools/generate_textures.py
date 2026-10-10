from pathlib import Path
from PIL import Image

ROOT = Path("src/main/resources/assets/wirelessredstone/textures/block")
ROOT.mkdir(parents=True, exist_ok=True)

# Original 16x16 pixel-art textures inspired by the user's wooden/metal wireless
# control unit reference: silver rim, dark wood, blue/red square controls, antenna.
DARK=(40,43,42,255); STEEL=(93,100,98,255); STEEL_L=(145,151,145,255)
STEEL_H=(184,188,178,255); WOOD=(105,72,40,255); WOOD_L=(139,99,56,255)
WOOD_D=(72,49,31,255); RED=(145,22,25,255); RED_L=(204,44,49,255)
BLUE=(49,78,173,255); BLUE_L=(83,116,215,255); BLACK=(25,26,25,255)
def canvas(c): return [[c for _ in range(16)] for _ in range(16)]
def save(name,p):
    im=Image.new("RGBA",(16,16))
    im.putdata([p[y][x] for y in range(16) for x in range(16)])
    im.save(ROOT/f"{name}.png")

# Top panel: silver frame, wood inset, and red signal strips.
p=canvas(WOOD)
for y in range(16):
    for x in range(16):
        if x in (0,15) or y in (0,15): p[y][x]=STEEL
        elif x in (1,14) or y in (1,14): p[y][x]=STEEL_H if (x+y)%2 else STEEL_L
        elif x in (2,13) or y in (2,13): p[y][x]=WOOD_D
        elif (x+y)%5==0: p[y][x]=WOOD_L
        elif (x*3+y)%7==0: p[y][x]=WOOD_D
for x in range(4,12):
    p[3][x]=WOOD_D
for y in range(4,12):
    p[y][3]=WOOD_D
for x,y in [(4,0),(11,0),(0,4),(0,11),(15,4),(15,11),(4,15),(11,15)]: p[y][x]=STEEL_H
for x,y in [(2,4),(2,5),(2,6),(13,9),(13,10),(13,11),(6,13),(7,13),(8,13),(9,13)]: p[y][x]=RED
save("wireless_sender_top",p)

# Side texture: horizontal dark timber between steel bands.
p=canvas(DARK)
for y in range(16):
    for x in range(16):
        if y in (0,1,14,15): p[y][x]=STEEL_H if x%3 else STEEL_L
        elif y in (2,3,12,13): p[y][x]=STEEL
        elif y in (4,11): p[y][x]=WOOD_D
        else: p[y][x]=WOOD_L if (x+y)%6==0 else WOOD if (x//3+y)%3 else WOOD_D
for x in (0,15): 
    for y in range(16): p[y][x]=STEEL
save("wireless_sender_side",p)

# Buttons have a raised steel bezel and a coloured inset.
for name,color,highlight in [("wireless_button_blue",BLUE,BLUE_L),("wireless_button_red",RED,RED_L)]:
    p=canvas(STEEL)
    for y in range(16):
        for x in range(16):
            if x in (0,15) or y in (0,15): p[y][x]=STEEL_L
            elif x in (1,14) or y in (1,14): p[y][x]=STEEL_H
            elif x in (2,13) or y in (2,13): p[y][x]=DARK
            elif 3<=x<=12 and 3<=y<=12: p[y][x]=color
            else: p[y][x]=STEEL
    for y in range(3,7):
        for x in range(3,8): p[y][x]=highlight
    for x in range(4,12): p[12][x]=DARK
    save(name,p)

# Receiver uses the same material language with one red status button.
p=canvas(WOOD)
for y in range(16):
    for x in range(16):
        if x in (0,15) or y in (0,15): p[y][x]=STEEL
        elif x in (1,14) or y in (1,14): p[y][x]=STEEL_H
        elif x in (2,13) or y in (2,13): p[y][x]=WOOD_D
        else: p[y][x]=WOOD_L if (x+y)%5==0 else WOOD
save("wireless_receiver_top",p)
save("wireless_receiver_side", [[STEEL if y in (0,1,14,15) else (WOOD_L if (x+y)%5==0 else WOOD_D if (x+y)%3==0 else WOOD) for x in range(16)] for y in range(16)])

# Antenna shaft and red cap, shaded as a vertical pixel texture.
p=canvas(WOOD_D)
for y in range(16):
    for x in range(16):
        if x in (0,15): p[y][x]=BLACK
        elif x in (1,2): p[y][x]=WOOD_L if y%3 else WOOD
        elif x in (13,14): p[y][x]=BLACK
        elif y<=2: p[y][x]=RED_L if x in (6,7,8,9) and y==1 else RED if x in range(4,12) else RED
        else: p[y][x]=WOOD if (x+y)%4 else WOOD_D
save("wireless_antenna",p)
save("wireless_receiver_indicator_off", [[WOOD_D if (x+y)%3 else RED for x in range(16)] for y in range(16)])
save("wireless_receiver_indicator_on", [[RED_L if (x+y)%3 else RED for x in range(16)] for y in range(16)])
print("Generated custom wood-and-steel wireless console textures.")
