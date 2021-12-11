package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items;

import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player.Player;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SawBlade.SawBlade;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.Items.Item;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.B2WorldCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Array;

public class RopePart extends Item {

    private boolean connectsaw;
    private RopeJoint[] ropeJoints;
    private RevoluteJoint[] joints;
    private float ropeScale;
    private Vector2 previousVelocity;
    private Vector2 previousPosition;
    private float angle;
    private Body sawConnectorB2body;
    private Vector2 previosSawConnectorPos;
    private Array<SawBlade> sawBlades;
    private int previousSawId;
    private SawBlade changedSawBlade;
    private Vector2 previousSawPosition;
    private Vector2 previousSawVelocity;
    private float sawAngle;

    public RopePart(PlayScreen screen, float x, float y, B2WorldCreator creator, float scale, Snake snake) {
        super(screen, x, y, creator, scale, snake);
        setRegion(atlas.findRegion("rope"), 0, 0, 128, 128);

        sawBlades = new Array<>();

        joints = new RevoluteJoint[3];
        ropeJoints = new RopeJoint[3];
        connectsaw = false;
        createNewJoints();
    }

    public void defineRope() {
        if (ropeScale == 0)
            ropeScale = 10f;
        BodyDef bdef = new BodyDef();
        if (b2body != null) {
            previousVelocity = b2body.getLinearVelocity();
            previousPosition = b2body.getPosition();
            previosSawConnectorPos = sawConnectorB2body.getPosition();
            angle = b2body.getAngle();
            world.destroyBody(b2body);
            world.destroyBody(sawConnectorB2body);
        }
        if(sawBlades.size != 0 && sawBlades.get(sawBlades.size - 1) != null) {
            if (!sawBlades.get(sawBlades.size - 1).isRemoveBody()) {
                previousSawPosition = sawBlades.get(sawBlades.size - 1).getB2body().getPosition();
                sawAngle = sawBlades.get(sawBlades.size - 1).getB2body().getAngle();
            }
        }

        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(getX() + getWidth() * snake.getScale(), getY() + getWidth() * snake.getScale());

        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getWidth() * 2 * snake.getScale() * 7 / BetterSnake.PPM), (getHeight() * 2 * snake.getScale() * 50 / BetterSnake.PPM));
        fdef.filter.categoryBits = BetterSnake.ROPE_BIT;
        fdef.filter.maskBits = BetterSnake.ROPE_BIT | BetterSnake.SAW_BIT;
        fdef.shape = shape;
        fdef.density = 0.01f;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(20 / BetterSnake.PPM);
        fdef.filter.categoryBits = BetterSnake.NOTHING_BIT;
        fdef.shape = circleShape;
        sawConnectorB2body = world.createBody(bdef);
        sawConnectorB2body.createFixture(fdef);


        if (previousVelocity != null)
            b2body.setLinearVelocity(previousVelocity);
        if (previousPosition != null) {
            b2body.setTransform(previousPosition, angle);
        }
        if (previosSawConnectorPos != null)
            sawConnectorB2body.setTransform(previosSawConnectorPos, 0);
        if(previousSawVelocity != null)
            sawBlades.get(sawBlades.size - 1).getB2body().setTransform(previousSawPosition, sawAngle);
        setScale(snake.getScale() * 2f);

    }

    public void createNewJoints() {

        if (joints[0] != null) {
            world.destroyJoint(joints[0]);
            world.destroyJoint(ropeJoints[0]);
        }

        defineRope();
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.localAnchorA.y = 0;
        jointDef.localAnchorB.y = getHeight() * snake.getScale();
        jointDef.bodyA = snake.getRopeB2body();
        jointDef.bodyB = b2body;
        joints[0] = (RevoluteJoint) screen.getWorld().createJoint(jointDef);

        jointDef.localAnchorA.y = -getHeight() * snake.getScale();
        jointDef.localAnchorB.y = 0;
        jointDef.bodyA = b2body;
        jointDef.bodyB = sawConnectorB2body;
        joints[1] = (RevoluteJoint) screen.getWorld().createJoint(jointDef);

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, getHeight() * snake.getScale());
        ropeJointDef.bodyA = snake.getRopeB2body();
        ropeJointDef.bodyB = b2body;
        ropeJointDef.maxLength = getHeight() * snake.getScale();
        ropeJoints[0] = (RopeJoint) screen.getWorld().createJoint(ropeJointDef);

        ropeJointDef.localAnchorA.set(0, -getHeight() * snake.getScale());
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.bodyA = b2body;
        ropeJointDef.bodyB = sawConnectorB2body;
        ropeJoints[1] = (RopeJoint) screen.getWorld().createJoint(ropeJointDef);

    }

    public void connectSaw() {
        RevoluteJointDef sawJointDef = new RevoluteJointDef();
        sawJointDef.bodyA = b2body;
        sawJointDef.bodyB = sawBlades.get(sawBlades.size - 1).getB2body();
        sawJointDef.localAnchorA.y = -getHeight() * snake.getScale();
        sawJointDef.localAnchorB.y = 0;
        joints[2] = (RevoluteJoint) screen.getWorld().createJoint(sawJointDef);

        RopeJointDef sawRopeJointDef = new RopeJointDef();
        sawRopeJointDef.localAnchorA.set(0, -getHeight() * snake.getScale());
        sawRopeJointDef.localAnchorB.set(0, 0);
        sawRopeJointDef.bodyA = b2body;
        sawRopeJointDef.bodyB = sawBlades.get(sawBlades.size - 1).getB2body();
        sawRopeJointDef.maxLength = 0;
        ropeJoints[2] = (RopeJoint) screen.getWorld().createJoint(sawRopeJointDef);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation((float) Math.toDegrees(b2body.getAngle()));
        if (sawBlades.size != 0 && (sawBlades.get(sawBlades.size - 1) != null && changedSawBlade != null || connectsaw)) {
            if(!sawBlades.get(sawBlades.size - 1).isRemoveBody())
                connectSaw();
        }
    }

    @Override
    public void defineItem() {
    }

    public Body getSawConnectorB2body() {
        return sawConnectorB2body;
    }

    public void addSawBlade(SawBlade sawBlade) {
        boolean identicalSawBladeInArray = false;
        for (int i = 0; i < sawBlades.size; i++) {
            if(sawBlade == sawBlades.get(i))
                identicalSawBladeInArray = true;
        }
        if(!identicalSawBladeInArray) {
            sawBlades.add(sawBlade);
        }
    }

    public void setConnectsaw(boolean connectsaw) {
        this.connectsaw = connectsaw;
    }

    public SawBlade getSawBlade() {
        if(sawBlades.size > 0)
            return sawBlades.get(sawBlades.size - 1);
        else {
            return null;
        }
    }

    public Array<SawBlade> getSawBlades() {
        return sawBlades;
    }

    public void setSawBlades(Array<SawBlade> sawBlades) {
        this.sawBlades = sawBlades;
    }
}
