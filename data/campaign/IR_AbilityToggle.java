package data.campaign;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseToggleAbility;
import com.fs.starfarer.api.impl.campaign.abilities.GraviticScanData;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.terrain.SlipstreamTerrainPlugin.Stream;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import data.hullmods.IR_ImprovisedRefinery;

public class IR_AbilityToggle extends BaseToggleAbility {

	public static float SLIPSTREAM_DETECTION_RANGE = 20000f;

	public static String COMMODITY_ID = Commodities.VOLATILES;
	public static float COMMODITY_PER_DAY = 1f;

	public static float DETECTABILITY_PERCENT = 50f;

	@Override
	protected void applyEffect(float arg0, float arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void cleanupImpl() {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getActivationText() {
		return "Ore Refinery activated";
	}

	@Override
	protected String getDeactivationText() {
		return null;
	}

	@Override
	protected void activateImpl() {
		IR_ImprovisedRefinery.EnbabledForPlayerFleet = true;
	}

	@Override
	protected void deactivateImpl() {
		IR_ImprovisedRefinery.EnbabledForPlayerFleet = false;
	}

	@Override
	public boolean showActiveIndicator() {
		return isActive();
	}

	@Override
	public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
		Color bad = Misc.getNegativeHighlightColor();
		Color gray = Misc.getGrayColor();
		Color highlight = Misc.getHighlightColor();

		String status = " (off)";
		if (turnedOn) {
			status = " (on)";
		}

		LabelAPI title = tooltip.addTitle(spec.getName() + status);
		title.highlightLast(status);
		title.setHighlightColor(gray);

		float pad = 10f;

		CampaignFleetAPI fleet = getFleet();
		List<FleetMemberAPI> fleetMemebers = fleet.getFleetData().getMembersListCopy();

		int oreProcessedPerDay = 0;
		for (FleetMemberAPI member : fleetMemebers) {
			oreProcessedPerDay += member.getVariant().hasHullMod("IR_ImprovisedRefinery")
					? IR_ImprovisedRefinery.getConversionRate(member,
							Global.getSettings().getHullModSpec("IR_ImprovisedRefinery"))
					: 0;
		}
		;

		tooltip.addPara("Toggles Ore Refinery hullmod for all your ships. ",
				pad);

		tooltip.addPara(
				"Currently capable of converting %s Ore into %s Rare Ore per day.",
				pad, highlight,
				"" + (int) oreProcessedPerDay,
				"" + IR_ImprovisedRefinery.getRecievedRareOre(fleet.getFleetData().getFleet().getFlagship(),
						oreProcessedPerDay));

		// tooltip.addPara("Increases the range at which the fleet can be detected by
		// %s.",
		// pad, highlight,
		// "" + (int) DETECTABILITY_PERCENT + "%");

		// if (getFleet() != null && getFleet().isInHyperspace()) {
		// tooltip.addPara("Can not function in hyperspace.", bad, pad);
		// } else {
		// tooltip.addPara("Can not function in hyperspace.", pad);
		// }

		// tooltip.addPara("Disables the transponder when activated.", pad);
		addIncompatibleToTooltip(tooltip, expanded);
	}

	public boolean hasTooltip() {
		return true;
	}

	@Override
	public EnumSet<CampaignEngineLayers> getActiveLayers() {
		return EnumSet.of(CampaignEngineLayers.ABOVE);
	}

	// @Override
	// public void advance(float amount) {
	// super.advance(amount);

	// if (data != null && !isActive() && getProgressFraction() <= 0f) {
	// data = null;
	// }
	// }

	@Override
	public boolean isUsable() {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null)
			return false;

		return !Misc.isInsideSlipstream(fleet);
		// return isActive() || !fleet.isInHyperspace();
	}

}
