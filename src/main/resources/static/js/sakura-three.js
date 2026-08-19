(function() {
    const canvas = document.getElementById('three-canvas');
    if (!canvas || !window.THREE) return;

    // Load GLTFLoader
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/three@0.169.0/examples/js/loaders/GLTFLoader.js';
    document.head.appendChild(script);

    script.onload = function() {
        initScene();
    };

    function initScene() {
        const scene = new THREE.Scene();
        scene.fog = new THREE.Fog(0xfff5fb, 10, 100);

        const renderer = new THREE.WebGLRenderer({
            canvas,
            antialias: true,
            alpha: true
        });
        renderer.setPixelRatio(2);
        renderer.shadowMap.enabled = true;

        const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 200);
        camera.position.set(0, 5, 15);

        // Lights
        scene.add(new THREE.AmbientLight(0xffe8f8, 0.8));
        const dirLight = new THREE.DirectionalLight(0xffd0f0, 1.2);
        dirLight.position.set(10, 15, 5);
        dirLight.castShadow = true;
        scene.add(dirLight);

        // Load sakura tree model
        const loader = new THREE.GLTFLoader();
        let treeModel;

        loader.load('/models/sakura-tree.glb', function(gltf) {
            treeModel = gltf.scene;
            treeModel.scale.set(3, 3, 3);  // Adjust size
            treeModel.position.set(0, 0, 0);
            treeModel.rotation.y = Math.PI * 0.1;
            treeModel.traverse(child => {
                if (child.isMesh) {
                    child.castShadow = true;
                    child.receiveShadow = true;
                }
            });
            scene.add(treeModel);
        }, undefined, function(error) {
            console.error('Model load error:', error);
        });

        // Ground
        const ground = new THREE.Mesh(
            new THREE.PlaneGeometry(100, 100),
            new THREE.MeshStandardMaterial({ color: 0xf0f8ff })
        );
        ground.rotation.x = -Math.PI / 2;
        ground.receiveShadow = true;
        scene.add(ground);

        // Petals
        const petalCount = 600;
        const petalGeo = new THREE.BufferGeometry();
        const petalPos = new Float32Array(petalCount * 3);
        const petalVel = new Float32Array(petalCount * 3);

        function resetPetal(i) {
            petalPos[i*3] = (Math.random()-0.5)*30;
            petalPos[i*3+1] = 10 + Math.random()*15;
            petalPos[i*3+2] = (Math.random()-0.5)*30;
            petalVel[i*3] = (Math.random()-0.5)*0.6;
            petalVel[i*3+1] = -0.8 - Math.random()*0.6;
            petalVel[i*3+2] = (Math.random()-0.5)*0.4;
        }

        for(let i=0; i<petalCount; i++) resetPetal(i);
        petalGeo.setAttribute('position', new THREE.BufferAttribute(petalPos, 3));

        const petals = new THREE.Points(petalGeo, new THREE.PointsMaterial({
            color: 0xff9acf, size: 0.22, transparent: true, opacity: 0.9
        }));
        scene.add(petals);

        function resize() {
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            renderer.setSize(window.innerWidth, window.innerHeight);
        }
        window.addEventListener('resize', resize);
        resize();

        let time = 0;
        function animate() {
            requestAnimationFrame(animate);
            time += 0.01;

            if (treeModel) {
                treeModel.rotation.y += 0.005;
            }

            // Animate petals
            for(let i=0; i<petalCount; i++) {
                const idx = i*3;
                petalPos[idx] += petalVel[idx] * 0.016;
                petalPos[idx+1] += petalVel[idx+1] * 0.016;
                petalPos[idx+2] += petalVel[idx+2] * 0.016;

                if (petalPos[idx+1] < -2) resetPetal(i);
            }
            petalGeo.attributes.position.needsUpdate = true;

            renderer.render(scene, camera);
        }
        animate();
    }
})();
