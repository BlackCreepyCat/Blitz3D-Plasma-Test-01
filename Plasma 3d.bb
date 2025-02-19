;Set these To what you want
Const Objects=40
Const ScrWidth=1024
Const ScrHeight=768
SeedRnd(MilliSecs())

;Open a screen And draw a blobby Energy Field with squared falloff
Graphics3D ScrWidth,ScrHeight,0,2

cam = CreateCamera()
MoveEntity(cam,0,0,-1)

;create the image/texture... 
Image=CreateTexture(512,512)
SetBuffer(TextureBuffer(Image))
For Radius#=1 To 512 Step .5
	Energy#=(Radius*Radius)/1024
	Color Energy,Energy,Energy
	Oval Radius/2.0,Radius/2.0,512-Radius,512-Radius     ;Try Rect
Next
SetBuffer(BackBuffer())

;the magic :)
Global render2d=CreatePivot() 
PositionEntity render2d,-Float(ScrWidth)/ScrHeight,1,0 
ScaleEntity render2d,2.0/ScrWidth,-2.0/ScrWidth,1

mainmesh=CreateMesh(render2d)
EntityBlend(mainmesh,3)
Dim mesh(Objects)
Global brush=CreateBrush()
BrushTexture(brush,Image)
Dim vertsx(3)
Dim vertsy(3)
vertsx(0)=0  : vertsy(0)=0
vertsx(1)=512: vertsy(1)=0
vertsx(2)=512: vertsy(2)=512
vertsx(3)=0  : vertsy(3)=512
	
For i=0 To Objects-1
	mesh(i)=CreateSurface(mainmesh)
	AddVertex(mesh(i),vertsx(0),vertsy(0),0,0,0)
	AddVertex(mesh(i),vertsx(1),vertsy(1),0,1,0,0)
	AddVertex(mesh(i),vertsx(2),vertsy(2),0,1,1,0)
	AddVertex(mesh(i),vertsx(3),vertsy(3),0,0,1,0)
	AddTriangle(mesh(i),0,1,2)
	AddTriangle(mesh(i),2,3,0)
Next	

;Set up the positions And movement directions/speeds/angles of Objects
Dim XPos#(Objects)
Dim YPos#(Objects)
Dim XMove#(Objects)
Dim YMove#(Objects)
Dim Rotation#(Objects)
Dim RotationSpeed#(Objects)

For Obj=0 To Objects-1
	XPos(Obj)=Rand(0,ScrWidth)
	YPos(Obj)=Rand(0,ScrHeight)
	XMove(Obj)=(Rnd(0,1)-0.5)*4
	YMove(Obj)=(Rnd(0,1)-0.5)*4
	Rotation(Obj)=Rnd(0,1)*360
	RotationSpeed(Obj)=Rnd(-.2,.2)
Next

Global tempPivot=CreatePivot(render2d)

;Do a demo
Repeat
	For Obj=0 To Objects-1
		;Move the Objects
		XPos(Obj)=XPos(Obj)+XMove(Obj)
		YPos(Obj)=YPos(Obj)+YMove(Obj)
	
		;Bounce the Objects off the screen edges
		If XPos(Obj)<0 Or XPos(Obj)>ScrWidth Then XMove(Obj)=-XMove(Obj)
		If YPos(Obj)<0 Or YPos(Obj)>ScrHeight Then YMove(Obj)=-YMove(Obj)
	
		;Rotate the Objects around their corners
		Rotation(Obj)=Rotation(Obj)+RotationSpeed(Obj)
		
		;Set Object Color based on coords/angles
		BrushColor(brush,1024-(Rotation(Obj) Mod 256),512-XPos(Obj),512-YPos(Obj))
		PaintSurface(mesh(obj),brush)
		
		;actually update
		RotateEntity(tempPivot,0,0,Rotation(Obj))
		PositionEntity(tempPivot,XPos(Obj),YPos(Obj),0)
		For i=0 To 3
			TFormPoint(vertsx(i),vertsy(i),0,tempPivot,render2d)
			VertexCoords(mesh(Obj),i,TFormedX(),TFormedY(),0)
		Next
		
	Next
	
	RenderWorld()
	Flip 
Until KeyHit(1)

;~IDEal Editor Parameters:
;~C#Blitz3D