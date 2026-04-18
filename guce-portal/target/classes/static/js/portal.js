/**
 * Données d'exemple pour démo rapide (mode dev, sans Kafka côté navigateur).
 */
const demo = {
  declaration: {
    correlationId: "corr-GUCE-2026-0418-001",
    declarantId: "TR-CI-778899",
    customsOfficeCode: "CIAB1",
    referenceNumber: "REF-DAU-DEMO-0001",
    payloadJson: JSON.stringify(
      {
        regime: "IM4",
        declarant: { nom: "Société Import Demo", ncc: "CI-NCC-12345" },
        lignes: [
          { sh: "100630", masseNetteKg: 24000, valeurFob: 12500000 },
          { sh: "870323", qte: 2, valeurFob: 18500000 },
        ],
        transport: { mode: "MARITIME", bl: "BL-ABJ-2026-8899" },
      },
      null,
      2
    ),
  },
  manifest: {
    manifestNumber: "MAN-ABJ-2026-0042",
    vesselName: "MV Côte des Épices",
    arrivalDate: new Date().toISOString(),
    linesJson: JSON.stringify(
      {
        conteneurs: [
          { numero: "MSCU1234567", type: "40HC", scelle: "DGDDI-99" },
          { numero: "TEMU9876543", type: "20DV", scelle: "DGDDI-100" },
        ],
      },
      null,
      2
    ),
  },
  authorization: {
    applicantId: "IMP-CI-445566",
    goodsDescription:
      "Blé dur en grains (100630) — 24 000 t — origine Union européenne — entrepôt sous douane Abidjan.",
    expiresAt: new Date(Date.now() + 90 * 86400000).toISOString(),
  },
  payment: {
    declarationId: "",
    amountXof: "3125000",
    amountPay: "3125000",
    channel: "WAVE",
  },
  inspection: {
    declarationId: "",
    channel: "ORANGE",
    scheduledAt: new Date(Date.now() + 3 * 86400000).toISOString(),
    notes: "Contrôle documentaire + visite physique zone portuaire.",
  },
  pcs: {
    containerNumber: "MSCU1234567",
    declarationId: "",
    movementType: "ENTREE",
    occurredAt: new Date().toISOString(),
    mainleveeId: "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
  },
  operator: {
    legalName: "Transit CI SARL",
    email: "contact@transit-ci.demo",
    role: "TRANSITAIRE",
  },
  notification: {
    channel: "EMAIL",
    target: "declarant@example.ci",
    body: "Votre DAU a été enregistrée. Référence : REF-DAU-DEMO-0001.",
  },
  audit: {
    declarationId: "",
    eventType: "DAU_SOUMISE",
    actor: "PORTAL-DEMO",
    payloadJson: JSON.stringify({ source: "guce-portal", demo: true }),
    occurredAt: new Date().toISOString(),
  },
};

function setVal(id, v) {
  const el = document.getElementById(id);
  if (el) el.value = v ?? "";
}

function fillDeclaration() {
  const d = demo.declaration;
  setVal("d-correlationId", d.correlationId);
  setVal("d-declarantId", d.declarantId);
  setVal("d-customsOfficeCode", d.customsOfficeCode);
  setVal("d-referenceNumber", d.referenceNumber);
  setVal("d-payloadJson", d.payloadJson);
}

function fillManifest() {
  const d = demo.manifest;
  setVal("m-manifestNumber", d.manifestNumber);
  setVal("m-vesselName", d.vesselName);
  setVal("m-arrivalDate", d.arrivalDate);
  setVal("m-linesJson", d.linesJson);
}

function fillAuthorization() {
  const d = demo.authorization;
  setVal("a-applicantId", d.applicantId);
  setVal("a-goodsDescription", d.goodsDescription);
  setVal("a-expiresAt", d.expiresAt);
}

function fillPayment() {
  const d = demo.payment;
  const id =
    document.getElementById("hint-last-declaration-id")?.textContent?.trim() ||
    crypto.randomUUID();
  setVal("p-declarationId-tax", id);
  setVal("p-declarationId-confirm", id);
  setVal("p-amountXof", d.amountXof);
  setVal("p-amount", d.amountPay);
  setVal("p-channel", d.channel);
}

function fillInspection() {
  const d = demo.inspection;
  const id =
    document.getElementById("hint-last-declaration-id")?.textContent?.trim() ||
    crypto.randomUUID();
  setVal("i-declarationId-orient", id);
  setVal("i-declarationId-sched", id);
  setVal("i-channel", d.channel);
  setVal("i-scheduledAt", d.scheduledAt);
  setVal("i-notes", d.notes);
}

function fillPcs() {
  const d = demo.pcs;
  const decl =
    document.getElementById("hint-last-declaration-id")?.textContent?.trim() || "";
  setVal("pcs-containerNumber", d.containerNumber);
  setVal("pcs-declarationId", decl);
  setVal("pcs-movementType", d.movementType);
  setVal("pcs-occurredAt", d.occurredAt);
  setVal("pcs-declarationId-ml", decl || crypto.randomUUID());
  setVal("pcs-mainleveeId", crypto.randomUUID());
}

function fillOperator() {
  const d = demo.operator;
  setVal("o-legalName", d.legalName);
  setVal("o-email", d.email);
  setVal("o-role", d.role);
}

function fillNotification() {
  const d = demo.notification;
  setVal("n-channel", d.channel);
  setVal("n-target", d.target);
  setVal("n-body", d.body);
}

function fillAudit() {
  const d = demo.audit;
  const decl =
    document.getElementById("hint-last-declaration-id")?.textContent?.trim() ||
    crypto.randomUUID();
  setVal("au-declarationId", decl);
  setVal("au-eventType", d.eventType);
  setVal("au-actor", d.actor);
  setVal("au-payloadJson", d.payloadJson);
  setVal("au-occurredAt", d.occurredAt);
}

document.addEventListener("DOMContentLoaded", () => {
  const key = document.body?.dataset?.activeTab;
  if (!key) return;
  const btn = document.querySelector(`button[data-tab-key="${key}"]`);
  if (btn && window.bootstrap) {
    try {
      new bootstrap.Tab(btn).show();
    } catch (e) {
      console.warn(e);
    }
  }
});
