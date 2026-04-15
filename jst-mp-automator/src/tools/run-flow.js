import { z } from 'zod';

export const name = 'mp_run_flow';

export const description =
  'Run a pre-defined YAML test flow by name. (Stub: flow engine not yet implemented.)';

export const inputSchema = z.object({
  flowName: z.string().describe('Name of the YAML flow file (without .yaml extension)'),
  params: z.record(z.string()).optional().describe('Parameters to pass to the flow'),
});

export async function handler(args) {
  return {
    content: [
      {
        type: 'text',
        text: `Flow engine not yet implemented. Flow: ${args.flowName}`,
      },
    ],
  };
}
