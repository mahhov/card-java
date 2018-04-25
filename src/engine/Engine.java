package engine;

import world.World;

class Engine {
	
	final int FRAME_SIZE = 800;
	
	World world;
	Camera camera;
	JavaPainter p;
	Control c;
	
	Engine() {
		System.out.println("Grided Engine");
		world = new World(100, 100, 100);
		camera = new Camera();
		//		c = new Control(FRAME_SIZE);
		p = new JavaPainter(FRAME_SIZE);
	}
	
	void cameraMove() {
		/*if (c.getKey('w')) {
			camera.x += camera.angleCos * camera.moveSpeed;
			camera.y -= camera.angleSin * camera.moveSpeed;
		}
		
		if (c.getKey('s')) {
			camera.x -= camera.angleCos * camera.moveSpeed;
			camera.y += camera.angleSin * camera.moveSpeed;
		}
		
		if (c.getKey('a')) {
			camera.x -= Math.cos(camera.angle + Math.PI / 2) * camera.moveSpeed;
			camera.y -= Math.sin(camera.angle + Math.PI / 2) * camera.moveSpeed;
		}
		
		if (c.getKey('d')) {
			camera.x += Math.cos(camera.angle + Math.PI / 2) * camera.moveSpeed;
			camera.y += Math.sin(camera.angle + Math.PI / 2) * camera.moveSpeed;
		}
		
		if (c.getKey('q')) {
			camera.angle -= camera.angleSpeed / 2;
		}
		
		if (c.getKey('e')) {
			camera.angle += camera.angleSpeed / 2;
		}
		
		if (c.getKey('z')) {
			camera.z += camera.moveSpeed / 2;
		}
		
		if (c.getKey('x')) {
			camera.z -= camera.moveSpeed / 2;
		}
		
		if (c.getKey('r')) {
			camera.angleZ -= camera.angleSpeed / 2;
		}
		
		if (c.getKey('f')) {
			camera.angleZ += camera.angleSpeed / 2;
		}
		
		// MOUSE CAMERA MOVEMENT
		
		camera.angle += c.getMouseMoveX() * camera.angleSpeed;
		camera.angleZ += c.getMouseMoveY() * camera.angleSpeed;
		
		camera.x = Math3D.maxMin(camera.x, world.width - 1, 0);
		camera.y = Math3D.maxMin(camera.y, world.height - 1, 0);
		
		// RECALCULATE CAMERA DATA
		camera.update();
		
		// LIGHTING CONTROLS
		
		if (c.getMouseDown(Control.MIDDLE))
			world.fillColumnInFrontOfCameraWithCubes(camera);
		
		if (c.getMouseDown(Control.LEFT))
			world.addLightInFrontOfCamera(camera);
		
		if (c.getMouseState(Control.RIGHT) == Control.PRESS)
			world.removeAllLights();
		
		// PAINTER MODIFY
		
		if (c.getKey('c'))
			p.drawBorders = !p.drawBorders;
		
		if (c.getKey('v'))
			p.whiteMode = !p.whiteMode;
			*/
	}
	
	void begin() {
		int frame = 0;
		long beginTime = 0, endTime;
		
		while (true) {
			cameraMove();
			world.draw(p.brush, camera);
			
			p.repaint();
			
			wait(10);
			endTime = System.nanoTime() + 1;
			if (endTime - beginTime > 1000000000l) {
				System.out.println(frame);
				frame = 0;
				beginTime = endTime;
			}
			frame++;
		}
	}
	
	public static void wait(int howLong) {
		try {
			Thread.sleep(howLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//		Math3D.load();
		new Engine().begin();
	}
}